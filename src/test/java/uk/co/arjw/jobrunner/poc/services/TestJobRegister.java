package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import uk.co.arjw.jobrunner.poc.TPDMTestData;
import uk.co.arjw.jobrunner.poc.TPDMTestData.TPDMTestFiles;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.JobProgress;
import uk.co.arjw.jobrunner.poc.model.TPDMMessage;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;
import uk.co.arjw.jobrunner.poc.services.FileJobStore;
import uk.co.arjw.jobrunner.poc.services.JobRegister;



public class TestJobRegister {

    private final String STORE_ROOT = "./target/test-register-store";
    private JobRegister jobRegister;
    private TPDMMessage msg1 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1);
    private TPDMMessage msg2 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_2);
    private TPDMMessage msg3 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_3);

    @BeforeEach
    public void init() throws Exception {                
        jobRegister = new JobRegister(new FileJobStore(STORE_ROOT));
    }

    @Test
    public void testRegisterNewJob() throws Exception {
        
        Job job = jobRegister.create(msg1);
        assertEquals(1, jobRegister.all().size());
        assertNotNull(job);
        assertNotNull(job.getId());
        assertNotNull(job.getCreatedDateTime());
        assertEquals(JobStatus.CREATED,job.getStatus());
        assertEquals(msg1,job.getMessage());
    }

    @Test
    public void testFetchActiveJobs() throws Exception {
        
        jobRegister.create(msg1);
        jobRegister.create(msg2);
        jobRegister.create(msg3);
        assertEquals(3, jobRegister.created().size());
    }

    @Test
    public void testProcessNewJob() throws Exception {
        
        Job job = jobRegister.create(msg1);
        assertEquals(1, jobRegister.all().size());
        job = jobRegister.process(job);
        assertEquals(1, jobRegister.active().size());
        assertEquals(JobStatus.ACTIVE, job.getStatus());
        assertEquals(msg1,job.getMessage());
        assertNotNull(jobRegister.getProgress(job));
    }

   

    @Test
    public void testFetchJobProgress() throws Exception {
        Job job = jobRegister.create(msg1);
        job = jobRegister.process(job);
        assertEquals(1, jobRegister.active().size());
        assertEquals(Job.JobStatus.ACTIVE, job.getStatus());
        JobProgress progress = jobRegister.getProgress(job); 
        assertNotNull(progress.getJob());
        assertFalse(progress.isComplete());
    }
}
    