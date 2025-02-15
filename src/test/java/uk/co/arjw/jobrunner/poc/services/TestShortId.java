package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.model.ShortId;

public class TestShortId {

    @Test
    public void testShortIdGeneration() throws Exception {
        String id = ShortId.croppedUUID();
        assertNotNull(id);
        assertTrue(id.matches("[a-z0-9]{10}"));
    }

    @Test
    public void testGenerate1000UniqueIdsFromUUID() throws Exception {
        
        HashSet<String> idSet = new HashSet<>();
        for(int i =0 ; i < 1000; i++) {
            idSet.add(ShortId.croppedUUID());
        }
        assertEquals(1000, idSet.size());
        
    }

}