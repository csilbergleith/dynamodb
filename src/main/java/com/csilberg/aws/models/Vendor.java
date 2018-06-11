package com.csilberg.aws.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;


@Data
public class Vendor {

    @JsonProperty("id")
    private String id;

    @JsonProperty("Offices")
    private VendorOffice Offices;
}
