package com.csilberg.aws.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

import java.util.Set;

@Data
@DynamoDBTable(tableName = "ProductCatalog")
public class ProductCatalog {
    @DynamoDBHashKey(attributeName = "Id")
    private Integer id;

    @DynamoDBAttribute(attributeName = "Title")
    private String title;

    @DynamoDBAttribute(attributeName = "isbn")
    private String ISBN;

    @DynamoDBAttribute(attributeName = "Authors")
    private Set<String> bookAuthors;

    @DynamoDBAttribute(attributeName = "Price")
    private int price;

    @DynamoDBAttribute(attributeName = "ProductCategory")
    private String productCategory;

    @DynamoDBAttribute(attributeName = "Description")
    private String description;

    @DynamoDBAttribute(attributeName = "BicycleType")
    private String bicycleType;

}
