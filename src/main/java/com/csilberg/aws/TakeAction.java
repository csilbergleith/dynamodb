package com.csilberg.aws;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.csilberg.aws.enums.ActionControl;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class TakeAction {

    DynamoClient dbClient;

    String message = "done";

    public TakeAction(DynamoClient dbClient) {
        this.dbClient = dbClient;
    }

    public String act (String action, String year, String title, String tableName, JsonObject requestBody) {

        ActionControl act = ActionControl.getAction(action);

        System.out.println("action: " + action);
        switch (action) {
            case "create-table":
                message = dbClient.createTable(tableName);
                break;
            case "add-item":
                message = dbClient.addItem(year, title);
                break;
            case "get-item":
                message = dbClient.getItem(year, title);
                break;
            case "list-tables":
                message = dbClient.listTables();
                break;
            case "delete-item":
                message = dbClient.deleteItem(year, title);
                break;
            case "delete-table":
                message = dbClient.deleteTable(tableName);
                break;
            case "product-addItem":
                message = dbClient.productItemAdd(requestBody);
                break;
            case "product-getItem":
                message = dbClient.productGetItem(requestBody);
                break;
            case "product-deleteItem":
                message = dbClient.productDeleteItem(requestBody);
                break;
            case "getItemMapper":
                message = dbClient.getItemMapper(requestBody);
                break;
            case "writeItemMapper":
                message = dbClient.writeItemMapper(requestBody);
                break;
            case "testMapper":
                message = dbClient.testMapper(requestBody);
                break;
            case "writeCatalogItem":
                message = dbClient.writeCatalogItem(requestBody);
                break;
            case "scan-item":
                message = dbClient.scanItem(year, title);
                break;
        }
        return message;
    }
}
