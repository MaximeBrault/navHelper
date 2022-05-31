package com.adapei.navhelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckpointTest {

    private Checkpoint checkpoint1;
    private Checkpoint checkpoint2;
    private Checkpoint checkpoint3;
    private String uri;

    @BeforeEach
    void setUp() {
        this.uri = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Gull_portrait_ca_usa.jpg/300px-Gull_portrait_ca_usa.jpg";
        checkpoint1 = new Checkpoint(50.028, 80.5646, "displayName", "Pronon", null);
        checkpoint2 = new Checkpoint(36.12354, 68.6542, "displayName2", "Pronon2", null);
        checkpoint3 = new Checkpoint(200, 44, "displayName3", "Pronon3", null);
    }

    @Test
    void equals() {
        Checkpoint checkpoint4 = new Checkpoint(50.028, 80.5646, "displayName", "Pronon", null);
        assertNotEquals(checkpoint1, checkpoint2);
        assertEquals(checkpoint1, checkpoint4);
        assertNotEquals(checkpoint1,null);
    }

    @Test
    void getUri(){
        assertNotEquals(checkpoint1, null);
        assertNotEquals(checkpoint2, this.uri);
        assertNotEquals(checkpoint1,checkpoint2);
    }
}
