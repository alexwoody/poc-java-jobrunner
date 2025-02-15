package uk.co.arjw.jobrunner.poc.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.TPDMTestData;
import uk.co.arjw.jobrunner.poc.TPDMTestData.TPDMTestFiles;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.JobProgress;
import uk.co.arjw.jobrunner.poc.model.TPDMRecord;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;

public class TestJobProgress {

    private JobProgress jobProgress;
    private JobProgress jobProgressJobTimeOut;
    private JobProgress jobProgressTOWithRecords;
    private JobProgress jobProgress20Percent;
    private JobProgress jobProgress80Percent;
    private JobProgress jobProgress100Percent;
    private JobProgress jobProgressDuplicates;

    @BeforeEach
    public void init() {
        
        Job job = Job.builder()
        .id("")
        .status(JobStatus.ACTIVE)
        .message(TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1))
        .createdDateTime(LocalDateTime.now())
        .timeout(15)
        .build();

        jobProgress = JobProgress.builder()
        .job(job)
        .processedRecords(new ArrayList<TPDMRecord>())
        .build();

        jobProgressJobTimeOut = JobProgress
        .builder()    
        .job(job.toBuilder()
                .createdDateTime(LocalDateTime.now().minusMinutes(1))
                .build())
        .processedRecords(new ArrayList<TPDMRecord>())
        .build();

        ArrayList<TPDMRecord> partialRecords = new ArrayList<>();
        partialRecords.add(job.getMessage().getRecords().get(0));
        partialRecords.add(job.getMessage().getRecords().get(2));
        jobProgressTOWithRecords = jobProgressJobTimeOut.toBuilder()  
            .processedRecords(partialRecords)
        .build();
        
        Job job2 = Job.builder()
        .id("")
        .status(JobStatus.ACTIVE)
        .message(TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_2))
        .createdDateTime(LocalDateTime.now())
        .timeout(15)
        .build();

        partialRecords = new ArrayList<>();
        partialRecords.add(job2.getMessage().getRecords().get(0));
        partialRecords.add(job2.getMessage().getRecords().get(2));
        jobProgress20Percent = jobProgress.toBuilder()    
        .job(job2)
        .processedRecords(partialRecords)
        .build();
    
        partialRecords = new ArrayList<>();
        partialRecords.add(job2.getMessage().getRecords().get(1));
        partialRecords.add(job2.getMessage().getRecords().get(3));
        partialRecords.add(job2.getMessage().getRecords().get(4));
        partialRecords.add(job2.getMessage().getRecords().get(5));
        partialRecords.add(job2.getMessage().getRecords().get(6));
        partialRecords.add(job2.getMessage().getRecords().get(9));
        jobProgress80Percent = jobProgress20Percent.toBuilder()    
        .job(job2)
        .processedRecords(partialRecords)
        .build();

        partialRecords = new ArrayList<>();
        partialRecords.add(job2.getMessage().getRecords().get(7));
        partialRecords.add(job2.getMessage().getRecords().get(8));
        jobProgress100Percent = jobProgress80Percent.toBuilder()    
        .job(job2)
        .processedRecords(partialRecords)
        .build();

        partialRecords = new ArrayList<>();
        partialRecords.add(job2.getMessage().getRecords().get(0));
        partialRecords.add(job2.getMessage().getRecords().get(2));
        jobProgressDuplicates = jobProgress80Percent.toBuilder()    
        .job(job2)
        .processedRecords(partialRecords)
        .build();
    }

    @Test
    public void testJobProgressNoRecords() {
        assertFalse(jobProgress.hasTimedOut());
        assertFalse(jobProgress.isComplete());
        assertFalse(jobProgress.isPartiallyComplete());
    }

    @Test
    public void testJobProgressTimedOutNoRecords() {
        assertTrue(jobProgressJobTimeOut.hasTimedOut());
        assertFalse(jobProgressJobTimeOut.isPartiallyComplete());
        assertFalse(jobProgressJobTimeOut.isComplete());
    }

    @Test
    public void testJobProgressTimedOutPartiallyComplete() {
        assertTrue(jobProgressTOWithRecords.hasTimedOut());
        assertTrue(jobProgressTOWithRecords.isPartiallyComplete());
        assertFalse(jobProgressTOWithRecords.isComplete());
    }

    @Test
    public void testJobProgress20PercentNoErrors() {
        assertFalse(jobProgress20Percent.hasTimedOut());
        assertTrue(jobProgress20Percent.isPartiallyComplete());
        assertEquals(20,jobProgress20Percent.getPercentageComplete());
        assertFalse(jobProgress20Percent.isComplete());
    }

    @Test
    public void testJobProgress80PercentNoErrors() {
        assertFalse(jobProgress80Percent.isComplete());
        assertFalse(jobProgress80Percent.hasTimedOut());
        assertTrue(jobProgress80Percent.isPartiallyComplete());
        assertEquals(80,jobProgress80Percent.getPercentageComplete());
    }

    @Test
    public void testJobProgressCompleted() {
        assertFalse(jobProgress100Percent.hasTimedOut());
        assertFalse(jobProgress100Percent.isPartiallyComplete());
        assertEquals(100,jobProgress100Percent.getPercentageComplete());
        assertTrue(jobProgress100Percent.isComplete());
    }

    @Test
    public void testJobProgressDuplicates() {
        assertFalse(jobProgressDuplicates.hasTimedOut());
        assertTrue(jobProgressDuplicates.isPartiallyComplete());
        assertEquals(80,jobProgressDuplicates.getPercentageComplete());
        assertFalse(jobProgressDuplicates.isComplete());
    }
}