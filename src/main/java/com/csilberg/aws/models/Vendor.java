package com.csilberg.aws.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Vendor {

    @JsonProperty("id")
    String id;

    @JsonProperty("Offices")
    VendorOffice Offices;
}
