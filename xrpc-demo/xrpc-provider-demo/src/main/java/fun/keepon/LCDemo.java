package fun.keepon;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author LittleY
 * @date 2024/3/11
 * @description TODO
 */
public class LCDemo {

    public int rob(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];

        if(n == 1)  return nums[0];
        if(n == 2)  return Math.max(nums[0], nums[1]);

        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++)
            dp[i] = Math.max(nums[i] + dp[i - 2], dp[i - 1]);

        return dp[n - 1];
    }
    public static void main(String[] args) {

        System.out.println(new LCDemo().rob(new int[]{2,1,1,2}));

    }
}
