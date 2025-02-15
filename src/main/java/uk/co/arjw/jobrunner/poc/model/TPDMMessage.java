package uk.co.arjw.jobrunner.poc.model;
// RequestId

// Batch Job Id
// Request Type

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class TPDMMessage
{
    private String fileName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Singular("records")
    private List<TPDMRecord> records; 
}



