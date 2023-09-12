package com.TestWork;

public class gcdOfStrings {

    public String gcdOfStrings1(String str1, String str2) {
        int length1 = str1.length();
        int length2 = str2.length();
        int i = 0;
        char[] str = new char[]{};
        while (i < length1 && i < length2){
            str[i] = str1.charAt(i);
            if(str[i] != str2.charAt(i)){
                return "";
            }
            
        }

        return "";
    }
}
