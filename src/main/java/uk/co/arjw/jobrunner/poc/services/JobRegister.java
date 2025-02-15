package uk.co.arjw.jobrunner.poc.services;

import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.ACTIVE;
import static uk.co.arjw.jobrunner.poc.model.Job.JobStatus.CREATED;
import static uk.co.arjw.jobrunner.poc.services.JobStore.isNotOwnedByNode;
import static uk.co.arjw.jobrunner.poc.services.JobStore.isOwnedByNode;
import static uk.co.arjw.jobrunner.poc.services.JobStore.isPoachable;
import static uk.co.arjw.jobrunner.poc.services.JobStore.isStatusOf;

import java.time.LocalDateTime;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.JobProgress;
import uk.co.arjw.jobrunner.poc.model.ShortId;
import uk.co.arjw.jobrunner.poc.model.TPDMMessage;
import uk.co.arjw.jobrunner.poc.services.JobStore;

public class JobRegister
{
    
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    private JobStore jobStore;

    
    @Getter
    private String id;

    public JobRegister(JobStore jobStore) {
        this("node1", jobStore);
    }

    public JobRegister(String nodeId, JobStore jobStore) {
        try {
            this.jobStore = jobStore;
            this.id = nodeId;
        } catch (Exception e) {
            LOG.error("Error creating JobStore",e);
        }
    }

    public Job create(TPDMMessage message) throws Exception {
            
        Job job = Job.builder()
            .id(ShortId.croppedUUID())
            .createdDateTime(LocalDateTime.now())
            .message(message)
            .status(CREATED)
            .nodeId(this.id)
            .build();
            
            jobStore.add(job);
                
            LOG.info("created job : {}", job.getId());

            return job;
    }

    public Job process(Job job) { 
        job = job.toBuilder().status(ACTIVE).build();
        jobStore.update(job);
        return job;
    }

    public List<Job> all() {
        return jobStore.getAll();
    }
    
    public List<Job> active() {
        return jobStore.filter(isStatusOf(ACTIVE));
    }

    public List<Job> created() {
        return jobStore.filter(isStatusOf(CREATED));
    }
    
    public List<Job> observed() {
		return jobStore.filter(isNotOwnedByNode(id));
	}

    public JobProgress getProgress(Job job) {
        return JobProgress.builder()
        .processedRecords(jobStore.getJobRecords(job))
        .job(job)
        .build();
    }

	public List<Job> managed() {
		return jobStore.filter(isOwnedByNode(id));
    }

	public List<Job> poachable() {
		return jobStore.filter(isPoachable(id));
	}

	public void poach(Job job) {
        jobStore.lock(job);
        jobStore.update(job.toBuilder().nodeId(this.id).build());
    }
}