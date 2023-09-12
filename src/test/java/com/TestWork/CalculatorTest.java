package com.TestWork;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CalculatorTest {

    private Calculator calculator = new Calculator();
    private int operand1;
    private int operand2;
    private int expected;

    public CalculatorTest(int operand1, int operand2, int expected) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = {
                {1, 2, 3},
                {5, 5, 10},
                {10, 5, 15},
                {0, 0, 0},
                {-1, 1, 0}
        };
        return Arrays.asList(data);
    }

    @Test
    public void testAdd() {
        calculator.add(operand1);
        calculator.add(operand2);
        assertEquals(expected, calculator.getResult());
    }

    @Test
    public void testSubstract() {
        calculator.add(operand1);;
        calculator.substract(operand2);
        assertEquals(expected, calculator.getResult());
    }

    @Test
    public void testMultiply() {
        calculator.add(operand1);
        calculator.multiply(operand2);
        assertEquals(expected, calculator.getResult());
    }

    @Test
    public void testDivide() {
        calculator.add(operand1);
        calculator.divide(operand2);
        assertEquals(expected, calculator.getResult());
    }

    @Test
    public void testSquare() {
        calculator.square(operand1);
        assertEquals(expected, calculator.getResult());
    }
}