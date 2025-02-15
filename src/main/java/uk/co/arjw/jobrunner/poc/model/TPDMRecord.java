package uk.co.arjw.jobrunner.poc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor 
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class TPDMRecord {
    private int index;
    private String recordId;
    private String externalRecordId;
    private String accountId;
    private String firstName;
    private String lastName;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String postCode;
    private String county;
    private String country;
    private String vendorName;
    private String offeredAmount;
}