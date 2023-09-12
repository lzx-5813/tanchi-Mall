package com.TestWork;

public class IsSubsequence {
    public static boolean isSubsequence(String s, String t) {
        int n = s.length();
        int m = t.length();
        int i = 0,j = 0;
        while (i < n && j < m){
            if(s.charAt(i) == t.charAt(j)){
                i++;
            }
            j++;
        }
        return i==n;
    }

    public static boolean isSubsequence2(String s, String t) {
        int n = s.length();
        int m = t.length();
        int[][] dp = new int[m+1][26];
        for (int i = 0; i < 26; i++) {
            dp[m][i] = m;
        }
        for (int i = m-1; i >= 0; i--) {
            for (int j = 0; j < 26; j++) {
                if(t.charAt(i) == 'a' + j){
                    dp[i][j] = i;
                }else
                    dp[i][j] = dp[i+1][j];
            }
        }
        int add = 0;
        for (int i = 0; i < n; i++) {
            if(dp[add][s.charAt(i)-'a'] == m){
                return false;
            }
            add = dp[add][s.charAt(i)-'a'] + 1;
        }
        return true;
    }
    public static void main(String[] args) {
        String s = "axc";
        String t = "ahbgdc";
        System.out.println(isSubsequence(s, t));
    }
}
