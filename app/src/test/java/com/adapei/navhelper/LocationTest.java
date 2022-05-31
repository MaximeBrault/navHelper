package com.adapei.navhelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.mapbox.geojson.Point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationTest {

    private Location a;
    private Location b;
    private Location c;

    @BeforeEach
    void setUp() {
        a = new Location(50.028, 80.5646, "displayName", "Pronon");
        b = new Location(36.12354, 68.6542, "displayName2", "Pronon2");
        c = new Location(200, 44, "displayName3", "Pronon3");
    }

    @Test
    void getLatitude() {
        assertEquals(a.getLatitude(), 50.028);
        assertEquals(b.getLatitude(), 36.12354);
        assertEquals(c.getLatitude(), 0.0);
    }

    @Test
    void getLongitude() {
        assertEquals(a.getLongitude(), 80.5646);
        assertEquals(b.getLongitude(), 68.6542);
    }

    @Test
    void equals() {
        Location a2 = new Location(50.028, 80.5646, "displayName", "Pronon");
        assertNotEquals(b, a);
        assertEquals(a2, a);
    }

    @Test
    void isNear() {
        Location c = new Location(47.758822, -2.859676, "a", "b");
        Location d = new Location(47.758835, -2.859794, "c", "d");
        //those are rougly 9m away

        Location e = new Location(47.757479, -2.859944, "e", "f");
        //150m away from c

        Location f = new Location(47.758841, -2.859836, "g", "h");
        //11m away. Near = 10m or less

        assertTrue(c.isNear(d));
        assertFalse(c.isNear(e));
        assertFalse(c.isNear(f));
    }

    @Test
    void getDistanceBetweenCoordinates() {
        Location c = new Location(47.758822, -2.859676, "a", "b");
        Location d = new Location(47.758835, -2.859794, "c", "d");
        //those are roughly 9m away

        Location e = new Location(47.757479, -2.859944, "e", "f");
        //150m away from c

        Location f = new Location(47.758841, -2.859836, "g", "h");
        //12m away. Near = 10m or less

        assertEquals(Math.round(Location.getDistanceBetweenCoordinates(c, e)), 151);
        assertEquals(Math.round(Location.getDistanceBetweenCoordinates(c, d)), 9);
        assertEquals(Math.round(Location.getDistanceBetweenCoordinates(c, f)), 12);
    }

    @Test
    void toPoint() {
        Point p = a.toPoint();
        assertEquals(p.latitude(), 50.028);
        assertEquals(p.longitude(), 80.5646);
    }
}
