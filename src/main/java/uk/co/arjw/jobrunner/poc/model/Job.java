package uk.co.arjw.jobrunner.poc.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Job {

	public enum JobStatus {
		CREATED,
		ACTIVE,
		COMPLETED
	};
	
	@EqualsAndHashCode.Include
	private String id;
	@EqualsAndHashCode.Include
	private String nodeId;
	
	@EqualsAndHashCode.Exclude
	private int timeout;

	@JsonFormat(shape = JsonFormat.Shape.STRING, with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	private JobStatus status;

	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@EqualsAndHashCode.Exclude
	private LocalDateTime createdDateTime;
	
	@EqualsAndHashCode.Exclude
	private TPDMMessage message;
	
}