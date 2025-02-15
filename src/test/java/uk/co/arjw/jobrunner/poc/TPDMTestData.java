package uk.co.arjw.jobrunner.poc;

import java.time.LocalDateTime;

import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.ShortId;
import uk.co.arjw.jobrunner.poc.model.TPDMMessage;
import uk.co.arjw.jobrunner.poc.model.TPDMRecord;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;

public class TPDMTestData
{
    public enum TPDMTestFiles {
        EMPTY_FILE,
        SAMPLE_FILE_1,
        SAMPLE_FILE_2,
        SAMPLE_FILE_3,
    }

    public static TPDMMessage getTPDMMessage(TPDMTestFiles file) {
        
        TPDMMessage.TPDMMessageBuilder builder =  TPDMMessage.builder();
        
        switch(file) {
            case SAMPLE_FILE_1:
                addRecords(builder, 4);
                break;
            case SAMPLE_FILE_2 :
                addRecords(builder, 10);
                break;
                case SAMPLE_FILE_3 :
                addRecords(builder, 20);
                break;
            case EMPTY_FILE :
                break;
        }
        return builder.fileName(file.name()).build();
    }

    private static void addRecords(TPDMMessage.TPDMMessageBuilder builder, int noOfRecords)
    {
        for(int i=0; i < noOfRecords; i++) {
            addRecord(builder, i);
        } 
    }

    private static void addRecord(TPDMMessage.TPDMMessageBuilder builder, int index)
    {
        builder.records(TPDMRecord.builder().index(index).build());
    }

    public static Job createTestJob() throws Exception {
        return createTestJob("node1", JobStatus.CREATED);
    }

    public static Job createTestJob(String nodeId) throws Exception {
        return createTestJob(nodeId, JobStatus.CREATED);
    }

    public static Job createTestTimeAdjustedJob(String nodeId, JobStatus status) throws Exception {
        Job job = createTestJob(nodeId, status);
        return job.toBuilder().createdDateTime(LocalDateTime.now().minusSeconds(job.getTimeout())).build();
    }

    public static Job createTestJob(String nodeId, JobStatus status) throws Exception {
        return Job.builder()
            .id(ShortId.croppedUUID())
            .createdDateTime(LocalDateTime.now())
            .message(TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1))
            .status(status)
            .nodeId(nodeId)
            .timeout(15)
            .build();
    }


}