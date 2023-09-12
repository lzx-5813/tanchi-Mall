package com.TestWork;

public class mergeAlternately {
    public static String mergeAlternately1(String word1, String word2) {
        int length1 = word1.length();
        int length2 = word2.length();
        int left = 0;
        int right = 0;
        StringBuilder word = new StringBuilder();
        while (left < length1 || right < length2) {
            if (left < length1) {
                word.append(word1.charAt(left));
                left++;
            }
            if (right < length2) {
                word.append(word2.charAt(right));
                right++;
            }
        }
        return word.toString();
    }

    public static void main(String[] args) {
        String word1 = "ab", word2 = "pqrs";
        String s = mergeAlternately1(word1, word2);
        System.out.println(s);
    }
}