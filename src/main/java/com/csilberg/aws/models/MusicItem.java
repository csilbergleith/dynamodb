package com.csilberg.aws.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "Music")
public class MusicItem {

    @DynamoDBHashKey(attributeName = "Artist")
    private String artist;

    @DynamoDBRangeKey(attributeName = "SongTitle")
    private String songTitle;

    @DynamoDBAttribute(attributeName = "AlbumTitle")
    private String albumTitle;

    @DynamoDBAttribute(attributeName = "Year")
    private int year;
}

