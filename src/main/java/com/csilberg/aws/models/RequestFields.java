package com.csilberg.aws.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class RequestFields {
    @JsonProperty("Id")
    private int id;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Price")
    private int price;

    @JsonProperty("ProductCatagory")
    private String productCategory;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("BicycleType")
    private String bicycleType;

    @JsonProperty("Brand")
    private String brand;

    @JsonProperty("Color")
    private Set<String> color;

    @JsonProperty("Authors")
    private Set<String> authors;

    @JsonProperty("ISBN")
    private String ISBN;

    @JsonProperty("InPublication")
    private boolean inPublication;

    @JsonProperty("PageCount")
    private int pageCount;

    @JsonProperty("InStock")
    private boolean inStock;

    @JsonProperty("Pictures")
    private Map<String, String> pictures;

    @JsonProperty("QuantityOnHand")
    private String quantityOnHand;

    @JsonProperty("RelatedItems")
    private Set<Integer> relatedItems;

    @JsonProperty("Reviews")
    private Map<String, List<String>> reviews;

}
