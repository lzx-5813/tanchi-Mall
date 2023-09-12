package com.TestWork;


public class Triangle {
        public static String classify(int a, int b, int c) {
            if (a <= 0 || b <= 0 || c <= 0)
                return "Not a triangle";
            if (a + b <= c || a + c <= b || b + c <= a)
                return "Not a triangle";
            if (a == b && b == c)
                return "Equilateral";
            if (a == b || b == c || a == c)
                return "Isosceles";
            return "Scalene";
        }
}
