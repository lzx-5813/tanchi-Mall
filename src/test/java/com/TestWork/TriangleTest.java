package com.TestWork;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

class TriangleTest {
    @Test
    public void testValidTriangles() {
        // Valid Input
        Assert.assertEquals("Equilateral", Triangle.classify(3, 3, 3));
        Assert.assertEquals("Isosceles", Triangle.classify(2, 2, 3));
        Assert.assertEquals("Isosceles", Triangle.classify(2, 3, 2));
        Assert.assertEquals("Isosceles", Triangle.classify(3, 2, 2));
        Assert.assertEquals("Scalene", Triangle.classify(3, 4, 5));
        Assert.assertEquals("Scalene", Triangle.classify(5, 12, 13));
    }

    @Test
    public void testInvalidTriangles() {
        // Invalid Input
        Assert.assertEquals("Not a triangle", Triangle.classify(0, 0, 0));
        Assert.assertEquals("Not a triangle", Triangle.classify(3, 4, -5));
        Assert.assertEquals("Not a triangle", Triangle.classify(1, 1, 2));
        Assert.assertEquals("Not a triangle", Triangle.classify(2, 4, 2));
        Assert.assertEquals("Not a triangle", Triangle.classify(7, 3, 2));
    }
}