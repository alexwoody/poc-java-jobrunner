package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;

import uk.co.arjw.jobrunner.poc.TPDMTestData;
import uk.co.arjw.jobrunner.poc.TPDMTestData.TPDMTestFiles;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.JobProgress;
import uk.co.arjw.jobrunner.poc.model.TPDMMessage;
import uk.co.arjw.jobrunner.poc.services.FileJobStore;
import uk.co.arjw.jobrunner.poc.services.JobRegister;


public class TestMultipleJobRegister {

    private final static String NODE_1_ID = "chase";
    private final static String NODE_2_ID = "marshall";
    private final String STORE_ROOT = "./target/test-multi-register-store";
    private JobRegister node1;
    private JobRegister node2;
    private FileJobStore fileJobStore1; 
    private FileJobStore fileJobStore2;       
    private TPDMMessage msg1 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1);
    private TPDMMessage msg2 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_2);
    private TPDMMessage msg3 = TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_3);



    @BeforeEach
    public void init() throws Exception {         
        fileJobStore1 = new FileJobStore(STORE_ROOT); 
        fileJobStore2 = new FileJobStore(STORE_ROOT);              
        node1 = new JobRegister(NODE_1_ID, fileJobStore1);
        node2 = new JobRegister(NODE_2_ID, fileJobStore2);
    }

    @Test
    public void testActualFetchJobMultiNode() throws Exception {
        Job job = node1.create(msg1);
        job = node1.process(job);
        assertEquals(1, node1.active().size());
        assertEquals(Job.JobStatus.ACTIVE, job.getStatus());
        Thread.sleep(10000);
        assertEquals(1, node2.active().size());
        JobProgress progress = node2.getProgress(job); 
        assertNotNull(progress.getJob());
        assertFalse(progress.isComplete());
    }
    
    @Test
    public void testActualFetchJobsByNodeMultiple() throws Exception {
        Job job = node1.create(msg1);
        job = node1.process(job);
        assertEquals(1, node1.managed().size());
        assertEquals(Job.JobStatus.ACTIVE, job.getStatus());
        assertEquals(node1.getId(), job.getNodeId());
        JobProgress progress = node1.getProgress(job); 
        assertNotNull(progress.getJob());
        assertFalse(progress.isComplete());
        assertEquals(0, node2.managed().size());
    }
    
    @Test
    public void testActualFetchOtherNodesJobs() throws Exception {
        Job job = node1.create(msg1);
        job = node1.process(job);
        assertEquals(NODE_1_ID, job.getNodeId());
        assertEquals(1, node1.managed().size());
        assertEquals(Job.JobStatus.ACTIVE, job.getStatus());
        assertEquals(node1.getId(), job.getNodeId());
        job = node2.create(msg2);
        job = node2.process(job);
        assertEquals(NODE_2_ID, job.getNodeId());
        job = node2.create(msg3);
        job = node2.process(job);
        assertEquals(NODE_2_ID, job.getNodeId());
        assertEquals(2, node2.managed().size());
        Thread.sleep(10000);
        assertEquals(2, node1.observed().size());
        assertEquals(1, node2.observed().size());
    }  
    
    @Test
    public void testActualPoachable() throws Exception {
        Job job = node2.create(msg1);
        LocalDateTime adjustedDate = LocalDateTime.now().minusSeconds(job.getTimeout() + 2);
        job = job.toBuilder().createdDateTime(adjustedDate).build();
        node2.process(job);
        String poachableJobId = job.getId();
        Thread.sleep(10000);
        assertEquals(0,node2.observed().size());
        List<Job> poachable =  node1.poachable();
        assertEquals(1, poachable.size());
        assertEquals(0, node1.managed().size());
        node1.poach(job);
        assertEquals(1, node1.managed().size());
        Thread.sleep(10000);
        assertEquals(1 ,node2.observed().size());
        Job lostJob = node2.observed().get(0);
        assertEquals(poachableJobId ,lostJob.getId());
        assertEquals(NODE_1_ID ,lostJob.getNodeId());

    }  
}
    