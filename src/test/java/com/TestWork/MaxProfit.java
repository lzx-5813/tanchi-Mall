package com.TestWork;

public class MaxProfit {
    public int maxProfit(int[] prices) {
        int max = 0;
        int mindata = Integer.MAX_VALUE;
        for (int i = 0; i < prices.length-1; i++) {
           if(prices[i] < mindata){
               mindata = prices[i];
           }else if(max < prices[i]-mindata){
               max = prices[i]-mindata;
           }
        }
        return max;
    }
}
