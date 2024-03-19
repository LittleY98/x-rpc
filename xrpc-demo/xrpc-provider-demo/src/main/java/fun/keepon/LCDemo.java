package fun.keepon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author LittleY
 * @date 2024/3/11
 * @description TODO
 */
public class LCDemo {

    public static void main(String[] args) throws InterruptedException {

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            System.out.println("即将关闭");
        }));

        while (true){
            System.out.println("111");
            Thread.sleep(1000);
        }
    }
}
