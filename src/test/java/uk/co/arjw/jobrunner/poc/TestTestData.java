package uk.co.arjw.jobrunner.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.TPDMTestData.TPDMTestFiles;
import uk.co.arjw.jobrunner.poc.model.TPDMMessage;

/**
 * 3PDM Message Accepted
 */
public class TestTestData {


    @Test
    public void testEmptyMessage() {
        
        TPDMMessage msg = TPDMTestData.getTPDMMessage(TPDMTestFiles.EMPTY_FILE);
        assertEquals(TPDMTestFiles.EMPTY_FILE.name(), msg.getFileName());
        assertEquals(0,msg.getRecords().size());
    }

    @Test
    public void testGetTPDMMessage() {
        
        TPDMMessage msg = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1);
        assertEquals(TPDMTestFiles.SAMPLE_FILE_1.name(), msg.getFileName());
        assertEquals(4,msg.getRecords().size());
    }
}