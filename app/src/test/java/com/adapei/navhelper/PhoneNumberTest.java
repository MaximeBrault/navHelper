package com.adapei.navhelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PhoneNumberTest {

    private PhoneNumber a;
    private PhoneNumber b;

    @BeforeEach
    void setUp() {
        a = new PhoneNumber("0123456789", null);
        b = new PhoneNumber("0123456789", "test");
    }

    @Test
    void getNumber() {
        assertEquals(a.getNumber(), "0123456789");
        assertEquals(b.getNumber(), "0123456789");
    }

    @Test
    void getName() {
        assertEquals(a.getName(), "");
        assertEquals(b.getName(), "test");
    }

    @Test
    void toStringTest() {
        assertEquals(a.toString(), "0123456789 - ");
        assertEquals(b.toString(), "0123456789 - test");
    }
}
