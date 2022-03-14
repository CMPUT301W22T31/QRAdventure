package com.example.qradventure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class accountTest {
    private Record record;
    private Record record2;
    private Account testAccount;
    private Account testAccount2;
    private QR testQR;
    private QR testQR2;

    @Before
    public void setup(){
        testAccount = new Account("TestUser", "TestEmail", "12345677890", "SomeQR", "OtherQR");
        testAccount2 = new Account("TestUser", "TestEmail", "12345677890", "SomeQR", "OtherQR");
        testQR = new QR("Some QR");
        testQR2 = new QR("other QR");
        record = new Record(testAccount, testQR);
        record2 = new Record(testAccount, testQR2);
    }


    @Test
    public void testAdd(){
        record = new Record(testAccount, testQR);
        testAccount.addRecord(record);
        assertTrue(testAccount.containsRecord(record));
        assertFalse(testAccount.addRecord(record));

    }

    // Equality is based off the USERNAME, not any other aspect of account
    @Test
    public void testEquals(){
        assertTrue(testAccount.equals(testAccount2));

    }

    @Test
    public void testContains(){
        assertFalse(testAccount.containsRecord(record));
        testAccount.addRecord(record);
        assertTrue(testAccount.containsRecord(record));
    }
    @Test
    public void testTotalScore(){
        testAccount.addRecord(record);
        testAccount.addRecord(record2);
        assertEquals(testAccount.getTotalScore(), record.getQRscore() + record2.getQRscore());

    }

    @Test
    public void testLowestQR(){
        testAccount.addRecord(record);
        testAccount.addRecord(record2);

        assertEquals(testAccount.getLowestQR(), record2.getQRscore());
    }

    @Test
    public void testHighestQR(){
        testAccount.addRecord(record);
        testAccount.addRecord(record2);

        assertEquals(testAccount.getHighestQR(), record.getQRscore());
    }

}
