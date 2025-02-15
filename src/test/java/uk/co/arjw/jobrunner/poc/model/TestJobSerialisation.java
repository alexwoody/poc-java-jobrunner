package uk.co.arjw.jobrunner.poc.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.TPDMTestData;
import uk.co.arjw.jobrunner.poc.TPDMTestData.TPDMTestFiles;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.ShortId;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;

public class TestJobSerialisation {

    private final ObjectMapper objectMapper = new ObjectMapper();

    String json = "{\"status\":\"ACTIVE\",\"id\":\"421909db6d\",\"createdDateTime\":\"2020-02-10 14:28:05\"," +
    "\"message\":{\"fileName\":\"SAMPLE_FILE_1\",\"records\":" +
    "[{\"index\":0,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":1,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":2,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":3,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}]}," 
    + "\"timeout\":0}";
    
    String j2 = "{\"createdDateTime\":\"2020-02-10 23:05:54\",\"records\":" +
    "[{\"index\":0,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":1,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":2,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}," +
    "{\"index\":3,\"recordId\":null,\"externalRecordId\":null,\"accountId\":null,\"firstName\":null,\"lastName\":null,\"addressLine1\":null,\"addressLine2\":null,\"addressLine3\":null,\"city\":null,\"postCode\":null,\"county\":null,\"country\":null,\"vendorName\":null,\"offeredAmount\":null}]" +
    "}";

    @Test
    public void testJobSerialisation() throws Exception {
        
        objectMapper.registerModule(new JavaTimeModule());
        String j = objectMapper.writeValueAsString(createTestJob());
        System.out.println(j);
    }

    @Test
    public void testJobDeserialisation() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.readValue(json, Job.class);
    }

    @Test
    public void testJobSer() throws Exception {
        
        objectMapper.registerModule(new JavaTimeModule());
        String j = objectMapper.writeValueAsString(DateSer.builder().createdDateTime(LocalDateTime.now()).build());
        System.out.println(j);
    }

    @Test
    public void testJobDeser() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.readValue(j2, DateSer.class);
    }


    private static Job createTestJob() throws Exception {
        return Job.builder()
            .id(ShortId.croppedUUID())
            .createdDateTime(LocalDateTime.now())
            .message(TPDMTestData.getTPDMMessage(TPDMTestFiles.SAMPLE_FILE_1))
            .status(JobStatus.CREATED)
            .build();
    }

}