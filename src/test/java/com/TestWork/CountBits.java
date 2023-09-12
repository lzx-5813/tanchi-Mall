package com.TestWork;

public class CountBits {
    public int[] countBits(int n) {
        int bit[] = new int[n+1];
        for (int i = 0; i <= n; i++) {
            bit[i] = countOnes(i);
        }
        return bit;
    }

    public int countOnes(int x){
        int ones = 0;
        while (x > 0) {
            x &= (x - 1);
            ones++;
        }
        return ones;
    }

}
