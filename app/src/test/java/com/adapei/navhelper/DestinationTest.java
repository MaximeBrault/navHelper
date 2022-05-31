package com.adapei.navhelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

class DestinationTest {

    private Destination destinationIcon1;
    private Destination destinationIcon2;
    private Destination destinationUri1;
    private Destination destinationUri2;
    private String uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        destinationIcon1 = new Destination("a","b","",49.531472, 0.091495, "Test1", "Foyer1");
        destinationIcon2 = new Destination("a",null,"boucherie",49.531472, 0.091495, "Test1", "Foyer1");
        this.uri = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Gull_portrait_ca_usa.jpg/300px-Gull_portrait_ca_usa.jpg";
        destinationUri1 = new Destination("a","b",this.uri,49.531472, 0.091495, "Test1", "Test1");
    }

    @Test
    void getRepresentation() {
        assertEquals(destinationIcon1.getRepresentation(), "");
        assertEquals(uri, destinationUri1.getRepresentation());
        assertEquals(destinationIcon2.getRepresentation(), "boucherie");
    }

    @Test
    void isRepresentedByIcon() {
        assertTrue(destinationIcon1.isRepresentedByIcon());
        assertTrue(destinationIcon2.isRepresentedByIcon());
        assertFalse(destinationUri1.isRepresentedByIcon());
    }

    @Test
    void getNickname() {
        assertEquals(destinationIcon1.getNickname(), "b");
        assertNull(destinationIcon2.getNickname());
    }

    @Test
    void hasANick() {
        assertTrue(destinationIcon1.hasANick());
        assertFalse(destinationIcon2.hasANick());
    }
}