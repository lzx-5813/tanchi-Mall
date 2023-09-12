package com.TestWork;

public class NewTest {
    static int x = 10;
    static {x+=5;
        System.out.println(2);}
    public static void main(String[] args) {
        System.out.println(x);
    }
    static {
        x /= 5;
        System.out.println(1);
    }
}
