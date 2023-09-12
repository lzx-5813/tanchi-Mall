package com.hmdp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MetroTicketTest {

    private String type;
    private String lineno;
    private int money;
    private String expected;

    public MetroTicketTest(String type, String lineno, int money, String expected) {
        this.type = type;
        this.lineno = lineno;
        this.money = money;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testCases() {
        return Arrays.asList(new Object[][]{
                {"OneTicket", "Line1", 5, "You have pay for the Line1. Please pick it up."},
                {"OneTicket", "Line2", 5, "You have pay for the Line2. Please pick it up."},
                {"OneTicket", "Line1", 10, "You have pay for the Line1. Please pick it up and the loose change."},
                {"OneTicket", "Line2", 10, "You have pay for the Line2. Please pick it up and the loose change."},
                {"OneTicket", "Line1", 15, "There has no loose change,Please pick up your money,Sorry!!!!"},
                {"OneTicket", "Line2", 15, "There has no loose change,Please pick up your money,Sorry!!!!"},
                {"MonthTicket", "Line1", 150, "You have not pay enough for the month ticket. Please pay 200 RMB."},
                {"MonthTicket", "Line2", 150, "You have not pay enough for the month ticket. Please pay 200 RMB."},
                {"MonthTicket", "Line1", 200, "You have pay for the Line1 month ticket. Please pick it up."},
                {"MonthTicket", "Line2", 200, "You have pay for the Line2 month ticket. Please pick it up."},
        });
    }

    @Test
    public void testOperate() {
        MetroTicket metroTicket = new MetroTicket();
        String result = metroTicket.Operate(type, lineno, money);
        Assert.assertEquals(expected, result);
    }
}