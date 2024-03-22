package fun.keepon.shutdown;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author LittleY
 * @date 2024/3/19
 * @description 服务关闭HOOK
 **/
@Slf4j
public class XRpcShutdownHook extends Thread{
    @Override
    public void run() {
        log.warn("Start preparing for closure");

        // 通过CAS，将挡板打开
        while (true){
            if (ShutDownAssist.BAFFLE.compareAndExchange(false, true)) {
                break;
            }
        }

        // 等待所有任务结束，最多等待10秒
        long start = System.currentTimeMillis();
        while (true) {
            long count = ShutDownAssist.EXECUTE_COUNTER.sum();
            if (count <= 0) {
                break;
            }

            log.info("There are {} more tasks to complete, please wait......", count);
            if (System.currentTimeMillis() - start >= 10000) {
                log.info("If the waiting time reaches 10s, it will be shut down directly");
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        log.warn("SYSTEM SHUTS DOWN IN 3 SECONDS......");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
