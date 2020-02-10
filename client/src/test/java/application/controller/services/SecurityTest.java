package application.controller.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SecurityTest {

    @Test
    public void hashTest(){
        String testString = "thisisatest";
        String hashedString = "a7c96262c21db9a06fd49e307d694fd95f624569f9b35bb3ffacd880440f9787";
        assertEquals(hashedString, Security.hashPassword(testString));
    }
}
