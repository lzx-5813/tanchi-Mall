package com.TestWork;

public class KMP {
    public static int[] kmpNext(String str2){
        int j  =0;
        int[] next = new int[str2.length()];
        for (int i = 1; i < str2.length(); i++) {
            while(j>0 && str2.charAt(i) != str2.charAt(j)){
                j = next[j-1];
            }
            if(str2.charAt(i) == str2.charAt(j)){
                j++;
            }
            next[i] = j;
        }
        return next;
    }
    public static int kmpMatch(String str1,String str2){
        int[] next = kmpNext(str2);
        for (int i = 0,j = 0; i < str1.length(); i++) {
            while(j>0 && str1.charAt(i) != str2.charAt(j)){
                j = next[j-1];
            }
            if (str1.charAt(i) == str2.charAt(j)){
                j++;
            }
            if(j == str2.length()) {
                return i - j + 1;
            }
        }

        return -1;
    }
    public static void main(String[] args) {
        String str1 = "BBC ABCDAB ABCDABCDABDE";
        String str2 = "ABCDABD";
        System.out.println(kmpMatch(str1, str2));
    }

}
