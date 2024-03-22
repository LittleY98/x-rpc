package fun.keepon.shutdown;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author LittleY
 * @date 2024/3/19
 * @description 关闭相关
 */
public class ShutDownAssist {

    /**
     * 任务执行挡板，默认false，即不阻止执行
     */
    public static final AtomicBoolean BAFFLE = new AtomicBoolean(false);

    /**
     * 用于记录当前还在执行的任务数量
     */
    public static final LongAdder EXECUTE_COUNTER = new LongAdder();
}
