package fun.keepon.protect.impl;

import fun.keepon.protect.RateLimiter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author LittleY
 * @date 2024/3/18
 * @description 滑动窗口限流算法
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    // 维护一个线程安全的队列，用于存储时间窗口内的请求时间戳
    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
    /**
     * 时间窗口大小，单位毫秒
     */
    private final long timeWindow;

    /**
     * 时间窗口内允许的最大请求数
     */
    private final int maxRequest;

    /**
     * 构造函数
     *
     * @param timeWindow 时间窗口大小，单位毫秒
     * @param maxRequest 时间窗口内允许的最大请求数
     */
    public SlidingWindowRateLimiter(long timeWindow, int maxRequest) {
        this.timeWindow = timeWindow;
        this.maxRequest = maxRequest;
    }

    public SlidingWindowRateLimiter() {
        this.timeWindow = 10;
        this.maxRequest = 10;
    }

    @Override
    public synchronized boolean allowRequest() {
        // 获取当前时间
        long now = System.currentTimeMillis();

        // 移除时间窗口之外的旧时间戳
        while (!queue.isEmpty() && (now - queue.peek() > timeWindow)) {
            queue.poll();
        }

        // 判断当前时间窗口内的请求是否超过限制
        if (queue.size() < maxRequest) {
            queue.offer(now);
            // 未超过限制，允许请求
            return true;
        }

        // 超过限制，拒绝请求
        return false;
    }
}
