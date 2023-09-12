package com.hmdp;

import org.junit.Before;

import static org.junit.Assert.assertEquals;

import org.junit.*;


public class MetroTicketTest2 {

    private MetroTicket metroTicket;

    @Before
    public void setup() {
        metroTicket = new MetroTicket();
        metroTicket.init();
    }

    @Test
    public void testOneTicketLine1Payment5() {
        String result = metroTicket.Operate("OneTicket", "Line1", 5);
        assertEquals("You have pay for the Line1. Please pick it up.", result);

    }

    @Test
    public void testOneTicketLine2Payment5() {
        String result = metroTicket.Operate("OneTicket", "Line2", 5);
        assertEquals("You have pay for the Line2. Please pick it up.", result);

    }

    @Test
    public void testOneTicketLine1Payment10() {
        String result = metroTicket.Operate("OneTicket", "Line1", 10);
        assertEquals("You have pay for the Line1. Please pick it up and the loose change.", result);

    }

    @Test
    public void testOneTicketLine2Payment10() {
        String result = metroTicket.Operate("OneTicket", "Line2", 10);
        assertEquals("You have pay for the Line2. Please pick it up and the loose change.", result);

    }

    @Test
    public void testOneTicketLine1PaymentInvalid() {
        String result = metroTicket.Operate("OneTicket", "Line1", 15);
        assertEquals("The money message is errno!!!", result);
    }

    @Test
    public void testOneTicketLine2PaymentInvalid() {
        String result = metroTicket.Operate("OneTicket", "Line2", 15);
        assertEquals("The money message is errno!!!", result);
    }

    @Test
    public void testMonthTicketLine1Payment200() {
        String result = metroTicket.Operate("MonthTicket", "Line1", 200);
        assertEquals("You have pay for the Line1 month ticket. Please pick it up.", result);
    }

    @Test
    public void testMonthTicketLine2Payment200() {
        String result = metroTicket.Operate("MonthTicket", "Line2", 200);
        assertEquals("You have pay for the Line2 month ticket. Please pick it up.", result);

    }

    @Test
    public void testMonthTicketLine1PaymentLessThan200() {
        String result = metroTicket.Operate("MonthTicket", "Line1", 150);
        assertEquals("You have not pay enough for the month ticket. Please pay 200 RMB.", result);
    }

    @Test
    public void testMonthTicketLine2PaymentLessThan200() {
        String result = metroTicket.Operate("MonthTicket", "Line2", 150);
        assertEquals("You have not pay enough for the month ticket. Please pay 200 RMB.", result);
    }

    @Test
    public void testInvalidInput() {
        String result = metroTicket.Operate("InvalidType", "Line1", 5);
        assertEquals("There has some input error!!!!!!", result);
    }
}