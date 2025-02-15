package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.arjw.jobrunner.poc.TPDMTestData.createTestJob;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.TPDMRecord;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;
import uk.co.arjw.jobrunner.poc.services.FileJobStore;
import uk.co.arjw.jobrunner.poc.services.JobStore;

public class TestFileJobStore {

    private FileJobStore jobStore;
    private FileJobStore populatedJobStore;
    private String TEST_STORE_ROOT = "./target/store";
    private Job PARTIALLY_PROCESSED_JOB;  
    private String TEST_RECORDS_STORE_ROOT = "./src/test/resources/store";

    @BeforeEach
    public void init() throws Exception {
        jobStore = new FileJobStore(TEST_STORE_ROOT);
        populatedJobStore = new FileJobStore(TEST_RECORDS_STORE_ROOT);
        PARTIALLY_PROCESSED_JOB = createTestJob().toBuilder().id("e81274a170").build(); 
    }

    @Test
    public void testAddStore() throws Exception {
        
        jobStore.add(createTestJob());
        assertEquals(1, jobStore.size());
    }

    @Test
    public void testFetchActiveJobs() throws Exception {
        
        jobStore.add(createTestJob());
        jobStore.add(createTestJob());
        assertEquals(2, jobStore.size());
    }

    @Test
    public void testRemoveJob() throws Exception {
        
        jobStore.add(createTestJob());
        jobStore.add(createTestJob());
        assertEquals(2, jobStore.size());
        Job job = jobStore.filter(JobStore.isStatusOf(JobStatus.CREATED)).get(0);
        jobStore.remove(job);
        assertEquals(1, jobStore.size());
    }

    @Test
    public void testGetJobRecords() throws Exception {
        List<TPDMRecord> records = populatedJobStore.getJobRecords(PARTIALLY_PROCESSED_JOB);
        assertEquals(3, records.size());
    }

    
}