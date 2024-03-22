package fun.keepon.protect.impl;

import fun.keepon.protect.RateLimiter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LittleY
 * @date 2024/3/18
 * @description 令牌桶限流器
 */
public class TokenBucketRateLimiter implements RateLimiter {
    private final long maxTokens;

    // 每秒填充的令牌数
    private final long refillRatePerSecond;
    private AtomicLong availableTokens;
    private long lastRefillTimestamp;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 构造函数
     * @param maxTokens 令牌桶的最大容量
     * @param refillRatePerSecond 每秒填充的令牌数
     */
    public TokenBucketRateLimiter(long maxTokens, long refillRatePerSecond) {
        this.maxTokens = maxTokens;
        this.refillRatePerSecond = refillRatePerSecond;
        this.availableTokens = new AtomicLong(maxTokens);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public TokenBucketRateLimiter() {
        this.maxTokens = 100;
        this.refillRatePerSecond = 100;
        this.availableTokens = new AtomicLong(maxTokens);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    /**
     * 填充令牌
     */
    private void refill() {
        long now = System.currentTimeMillis();
        // 计算距离上次填充过去的时间（以秒为单位）
        long elapsedTimeInSeconds = (now - lastRefillTimestamp) / 1000;
        if (elapsedTimeInSeconds > 0) {
            // 计算需要添加的令牌数
            long tokensToAdd = elapsedTimeInSeconds * refillRatePerSecond;
            // 确保令牌数不超过最大值
            long newTokenCount = Math.min(maxTokens, availableTokens.get() + tokensToAdd);
            availableTokens.set(newTokenCount);
            lastRefillTimestamp = now;
        }
    }

    @Override
    public boolean allowRequest() {
        lock.lock();
        try {
            refill();
            if (availableTokens.get() > 0) {
                availableTokens.decrementAndGet();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
