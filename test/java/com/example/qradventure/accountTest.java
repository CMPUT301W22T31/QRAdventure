package com.example.qradventure;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class accountTest {
    private Record record;
    private Account testAccount;
    private QR testQR;

    @Before
    public void setup(){
        testAccount = new Account("TestUser", "TestEmail", "12345677890", "SomeQR", "OtherQR");
//      QR testQR = new QR("Some QR");
//      record = new Record(testAccount, testQR);
    }


    @Test
    public void testAdd(){
        record = new Record(testAccount, testQR);
        testAccount.addRecord(record);
        assertTrue(testAccount.containsRecord(record));
        assertFalse(testAccount.addRecord(record));

    }

}
