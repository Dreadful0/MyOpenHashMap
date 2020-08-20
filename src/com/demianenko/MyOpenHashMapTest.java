package com.demianenko;

import  org.junit.jupiter.api.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MyOpenHashMap
 */
class MyOpenHashMapTest {

    MyOpenHashMap map;
    Random random = new Random();

    @BeforeEach
    void setUp() {
        map = new MyOpenHashMap(1);
    }

    @AfterEach
    void tearDown() {
        map = null;
    }

    @Test
    void init(){
        assertThrows(IllegalArgumentException.class, () -> {
            map = new MyOpenHashMap(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            map = new MyOpenHashMap(MyOpenHashMap.DEFAULT_INITIAL_CAPACITY,
                    1.5f, MyOpenHashMap.DEFAULT_MULTIPLIER);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            map = new MyOpenHashMap(MyOpenHashMap.DEFAULT_INITIAL_CAPACITY,
                    MyOpenHashMap.DEFAULT_LOAD_FACTOR, 0.5f);
        });
    }

    @Test
    void size() {
        assertEquals(0, map.size());
        for (int i=0; i<20; i++){
            assertNull(map.put(random.nextInt(1000)+i*1000,random.nextInt(100)));
        }
        System.out.println(map);
        assertEquals(20, map.size());
    }

    @Test
    void put() {
        assertEquals(0, map.size());
        for (int i=0; i<12; i++){
            assertNull(map.put(random.nextInt(1000)+(i+1)*1000,random.nextInt(100)));
        }
        System.out.println(map);
        assertEquals(12, map.size());
        assertNull(map.put(15000, 22));
        assertEquals(13, map.size());
        assertEquals(22,map.put(15000,23));
        assertEquals(13,map.size());
        assertNull(map.put(-1000, 111));
        assertEquals(14, map.size());
        assertNull(map.put(0, 111));
        assertEquals(15, map.size());
    }

    @Test
    void get() {
        assertEquals(0, map.size());
        assertNull(map.put(1555, -555));
        assertNull(map.put(1666, 666));
        assertNull(map.put(1777, 777));
        assertNull(map.put(1888, -888));
        assertNull(map.put(1999, 999));
        assertEquals(777,map.put(1777,7777));
        System.out.println(map);
        assertEquals(5,map.size());
        assertEquals(-888, map.get(1888));
        assertEquals(7777,map.get(1777));
        assertNull(map.get(1444));
    }
}