package com.csilberg.aws.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class VendorOffice {

    @JsonProperty("Name")
    String Name;

    @JsonProperty("Offices")
    List<String> Offices;

}
