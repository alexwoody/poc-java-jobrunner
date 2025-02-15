package uk.co.arjw.jobrunner.poc.services;

import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.COMPLETED;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.TPDMRecord;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;

public interface JobStore extends EventListener {

    void add(Job job);
    void update(Job job); 
    void remove(Job job);
    void lock(Job job);
    List<Job> filter(Predicate<Job> predicate);
    Job filterById(String id);
    List<Job> getAll();
    int size();
	List<TPDMRecord> getJobRecords(Job job);

    public static Predicate<Job> isOwnedByNode(String id) {
        return p -> p.getNodeId() == id;
    }

    public static Predicate<Job> isNotOwnedByNode(String id) {
        return p -> p.getNodeId() != id;
    }

    public static Predicate<Job> isStatusOf(JobStatus status) {
        return p -> p.getStatus() == status;
    }
    
    public static Predicate<Job> isCompleted() {
        return p -> p.getStatus() != COMPLETED;
    }

    public static Predicate<Job> hasTimedout() {
        return p -> p.getCreatedDateTime().plusSeconds(p.getTimeout()).isBefore(LocalDateTime.now());
    }

    public static Predicate<Job> isPoachable(String id) {
        return p -> 
        p.getNodeId() != id
        && p.getStatus() != COMPLETED 
        && p.getCreatedDateTime().plusSeconds(p.getTimeout()).isBefore(LocalDateTime.now());
        
    }

 }