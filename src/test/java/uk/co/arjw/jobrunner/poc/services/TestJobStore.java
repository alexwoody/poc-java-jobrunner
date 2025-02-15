package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.arjw.jobrunner.poc.TPDMTestData.createTestJob;
import static uk.co.arjw.jobrunner.poc.TPDMTestData.createTestTimeAdjustedJob;
import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.ACTIVE;
import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.COMPLETED;
import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.CREATED;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;
import uk.co.arjw.jobrunner.poc.services.JobStore;

public class TestJobStore {

    private final static String NODE_1_ID = "chase";
    private final static String NODE_2_ID = "marshall";
    private List<Job> jobs;

    @BeforeEach
    public void init() throws Exception {
        jobs = new ArrayList<>();
        jobs.add(createTestJob(NODE_1_ID));
        jobs.add(createTestJob(NODE_1_ID));
        jobs.add(createTestJob(NODE_2_ID));
        jobs.add(createTestJob(NODE_1_ID));
        jobs.add(createTestJob(NODE_2_ID));
        jobs.add(createTestJob(NODE_1_ID, ACTIVE));
        jobs.add(createTestJob(NODE_2_ID, ACTIVE));
        jobs.add(createTestJob(NODE_2_ID, ACTIVE));
        jobs.add(createTestJob(NODE_1_ID, ACTIVE));
        jobs.add(createTestJob(NODE_1_ID, COMPLETED));
        jobs.add(createTestJob(NODE_2_ID, COMPLETED));
        jobs.add(createTestTimeAdjustedJob(NODE_1_ID, CREATED));
        jobs.add(createTestTimeAdjustedJob(NODE_2_ID, CREATED));
        jobs.add(createTestTimeAdjustedJob(NODE_2_ID, ACTIVE));
    } 

    @Test
    public void testJobPredicateCreated() {
        Predicate<Job> predicate = JobStore.isStatusOf(JobStatus.CREATED);
        assertEquals(7, jobs.stream().filter(predicate).count());
    } 

    @Test
    public void testJobPredicateActive() {
        
        Predicate<Job> predicate = JobStore.isStatusOf(JobStatus.ACTIVE);
        assertEquals(5, jobs.stream().filter(predicate).count());
    } 

    @Test
    public void testJobPredicateIsPoachable() {
        
        Predicate<Job> predicate = JobStore.isPoachable(NODE_1_ID);
        assertEquals(2, jobs.stream().filter(predicate).count());
    } 

    @Test
    public void testJobPredicateCompleted() {
        
        Predicate<Job> predicate = JobStore.isStatusOf(JobStatus.COMPLETED);
        assertEquals(2, jobs.stream().filter(predicate).count());
    } 

}