package com.TestWork;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ComputerTest {
    public  int screensSold;
    public int cpusSold;
    public int hardDrivesSold;
    public double expected;

    public ComputerTest(int screensSold, int cpusSold, int hardDrivesSold, double expected) {
        this.screensSold = screensSold;
        this.cpusSold = cpusSold;
        this.hardDrivesSold = hardDrivesSold;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {
        return Arrays.asList(new Object[][]{
                {0,0,0,0.0},
                {50,30,10,11800.0},
                {100,10,3,17380.0},
        });
    }
    @Test
    public void test1()
    {
        Computer computer = new Computer();
        double commission = computer.Calculator(screensSold, cpusSold, hardDrivesSold);
        Assert.assertEquals(expected, commission,0.01);
    }

}