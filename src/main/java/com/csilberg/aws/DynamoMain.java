package com.csilberg.aws;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import static io.vertx.core.Vertx.vertx;

public class DynamoMain extends AbstractVerticle {

    private  String action = "";
    private  String title;
    private  String year;
    private  String tableName;
    private  String result;

    @Override
    public void start(Future<Void> fut) {

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .build();

        DynamoClient dbClient = new DynamoClient(dynamoDB);

        TakeAction takeAction = new TakeAction(dbClient);

        HttpServer server = vertx.createHttpServer()
                .requestHandler(req ->{

            HttpServerResponse response = req.response();

            long startTime = System.currentTimeMillis();

            if(req.query() != null) {
                if (req.getParam("action") != null) { action = req.getParam("action");}
                if (req.getParam("title") != null) { title = req.getParam("title");}
                if(req.getParam("year") != null) { year = req.getParam("year");}
                if(req.getParam("table") != null) { tableName = req.getParam("table");}
            }

            req.bodyHandler(body ->{
                if(body.length() > 0) {
                    final JsonObject jsonBody = body.toJsonObject();

                    action = jsonBody.getString("action") != null ? jsonBody.getString("action") : action;
                    title = jsonBody.getString("title") != null ? jsonBody.getString("title") : title;
                    year = jsonBody.getString("year") != null ? jsonBody.getString("year") : year;

                    vertx.executeBlocking(future -> {
                        result = takeAction.act( action, year, title, tableName, jsonBody);
                        future.complete(result);
                    }, res -> {
                        sendResponse(action, res.result().toString(), response, startTime);
                    });
                }

            });
        });

        server.listen(8080);
        System.out.println("Starting Server. Listening on 8080");
    }

    private void sendResponse (String action, String result, HttpServerResponse response, long startTime) {

        System.out.println(System.currentTimeMillis() - startTime +  " ms");
        response.putHeader("content-type", "text/html")
                .setStatusCode(200)
                .end("<p>" + action + "</p>" + "<br/><p>" + result + "</p>");
    }
}

