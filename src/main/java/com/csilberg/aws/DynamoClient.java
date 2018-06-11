package com.csilberg.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.csilberg.aws.models.MusicItem;
import com.csilberg.aws.models.ProductCatalog;
import com.csilberg.aws.models.RequestFields;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DynamoClient {

    AmazonDynamoDB dynamoDB;

    public DynamoClient(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    public String createTable( String tableName) {

        System.out.println("Attempting to create table; please wait...");
        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition("keyId", ScalarAttributeType.N))
                .withKeySchema(new KeySchemaElement("keyId", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("list", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("list", KeyType.RANGE))
                .withProvisionedThroughput(new ProvisionedThroughput(new Long (1), new Long(1)))
                .withTableName(tableName);

        try {
            CreateTableResult result = dynamoDB.createTable(request);

            System.out.println("Success.  Table status: " + result.toString());
            return "Success.  Table status: " + result.toString();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
            return "Unable to create table: " + e.getMessage();
        }
    }

    public String addItem (String year, String title) {

        final String tableName = "Movies";
        HashMap<String, AttributeValue> item_values =
                new HashMap<String, AttributeValue>();

        item_values.put("year", new AttributeValue().withN(year));
        item_values.put("title", new AttributeValue(title));

        try {
            PutItemResult result = dynamoDB.putItem(tableName, item_values).withConsumedCapacity(new ConsumedCapacity());
            System.out.println("Recorded Added");
            return "Item Added. ConsumedCapacity: " + result.getConsumedCapacity();
        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "error: " + e.getErrorMessage();
        }
    }

    public String getItem(String year, String title) {

        final String tableName = "Movies";
        StringBuilder dbResponse = new StringBuilder();

        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put("year", new AttributeValue().withN(year));
        key_to_get.put("title", new AttributeValue(title));

        GetItemRequest request = new GetItemRequest()
                .withKey(key_to_get)
                .withTableName(tableName)
                .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        try {
            GetItemResult result =
                    dynamoDB.getItem(request);

            Map<String, AttributeValue> returned_item = result.getItem();

            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                for (String key : keys) {
                    System.out.printf("%s: %s\n",
                            key, returned_item.get(key).toString());
                    dbResponse.append(key + ": ");
                    dbResponse.append(returned_item.get(key).toString());
                    dbResponse.append("<br/>");
                }
            } else {
                System.out.println("No value found for key ");
            }

        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "Service Error: " + e.getErrorMessage();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            return "Service Error: " + e.getMessage();

        }

        return dbResponse.toString();
    }

    public String getItemMapper(JsonObject requestBody) {

        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);

        String returnMessage;
        MusicItem keySchema = new MusicItem();

        String artist = requestBody.getString("Artist") == null ? null : requestBody.getString("Artist");
        String title =  requestBody.getString("Title") == null ? null : requestBody.getString("Title");
        int year = requestBody.getInteger("year") == null ? 0 : requestBody.getInteger("year");

        keySchema.setArtist(artist);
        keySchema.setSongTitle(title);

        try {
            MusicItem result = mapper.load(keySchema);

            if (result != null) {
                returnMessage = String.format("The song was released in %d", result.getYear());
                System.out.println("mapper result: " + result.getSongTitle());
                System.out.println(returnMessage);
            } else {
                returnMessage = "No matching song found ";
                System.out.println(returnMessage);
            }

        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "Service Error: " + e.getErrorMessage();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            return "Service Error: " + e.getMessage();

        }

        return returnMessage;
    }

    public String writeItemMapper(JsonObject requestBody) {

        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);

        String returnMessage;

        MusicItem musicItem = new MusicItem();

        String artist = requestBody.getString("Artist") == null ? null : requestBody.getString("Artist");
        String title =  requestBody.getString("Title") == null ? null : requestBody.getString("Title");
        String albumTitle = requestBody.getString("Album") == null ? null : requestBody.getString("Album");
        int year = requestBody.getInteger("Year") == null ? 0 : requestBody.getInteger("Year");

        musicItem.setArtist(artist);
        musicItem.setSongTitle(title);
        musicItem.setYear(year);
        musicItem.setAlbumTitle(albumTitle);

        try {
            mapper.save(musicItem);
        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "Service Error: " + e.getErrorMessage();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            return "Service Error: " + e.getMessage();

        }

        return "Song " + title + " added via mapper";
    }

    public String writeCatalogItem(JsonObject requestBody) {

        ProductCatalog catalogItem = new ProductCatalog();

        catalogItem.setId(requestBody.getInteger("Id"));
        catalogItem.setTitle(requestBody.getString("Title"));
        catalogItem.setISBN(requestBody.getString("ISBN"));

        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
        try {
            mapper.save(catalogItem);
        }catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "Service Error: " + e.getErrorMessage();
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            return "Service Error: " + e.getMessage();

        }
        return String.format("Item %d saved", requestBody.getInteger("Id"));
    }

    public String testMapper(JsonObject requestBody) {

        final int id = requestBody.getInteger("Id");

        ProductCatalog item = new ProductCatalog();
        item.setId(id);
        item.setTitle("Book " + Integer.toString(id));
        item.setISBN("611-1111111111");
        item.setPrice(100);
        item.setDescription("This is a test of description");
        item.setBookAuthors(new HashSet<String>(Arrays.asList("Author1", "Author2")));

        if (requestBody.getString("Title") != null) {
            item.setTitle(requestBody.getString("Title"));
        }

        // Save the item (book).
        DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB);
        mapper.save(item);

        // Retrieve the item.
        ProductCatalog itemRetrieved = mapper.load(ProductCatalog.class, id);
        System.out.println("Item retrieved:");
        System.out.println(itemRetrieved);

        // Update the item.
        itemRetrieved.setISBN("622-2222222222");
        itemRetrieved.setBookAuthors(new HashSet<String>(Arrays.asList("Author1", "Author3")));
        mapper.save(itemRetrieved);
        System.out.println("Item updated:");
        System.out.println(itemRetrieved);

        // Retrieve the updated item.
//        DynamoDBMapperConfig config = new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT);
        ProductCatalog updatedItem = mapper.load(ProductCatalog.class, id);
        System.out.println("Retrieved the previously updated item:");
        System.out.println(updatedItem);

//         Delete the item.
        if (requestBody.getBoolean("keep") != null) {
            if (requestBody.getBoolean("keep") == false) {
                mapper.delete(updatedItem);
            }
        }

        // Try to retrieve deleted item.
        ProductCatalog deletedItem = mapper.load(ProductCatalog.class, updatedItem.getId());
        if (deletedItem == null) {
            System.out.println("Done - Sample item is deleted.");
        }
        else {
            System.out.println(String.format("Sample item %d still exists", deletedItem.getId()));
        }

        return "testMapper Done";
    }


    public String listTables() {
        System.out.println("Table List:");

        ListTablesRequest request;
        boolean more_tables = true;
        String last_name = null;
        String result = "";
        StringBuilder sbTables = new StringBuilder();
        JsonArray array = new JsonArray();

        while (more_tables) {
            try {
                if (last_name == null) {
                    request = new ListTablesRequest().withLimit(10);
                } else {
                    request = new ListTablesRequest()
                            .withLimit(10)
                            .withExclusiveStartTableName(last_name);
                }
                ListTablesResult table_list = dynamoDB.listTables(request);
                List<String> table_names = table_list.getTableNames();

                if (table_names.size() > 0) {
                    for (String curr_name : table_names) {
                        System.out.printf("* %s\n", curr_name);
                        sbTables.append("* " + curr_name);
                        sbTables.append("<br/>");
                        array.add(curr_name);
                    }
                } else {
                    System.out.println("No tables found");
                }

                last_name = table_list.getLastEvaluatedTableName();
                if (last_name == null) {
                    more_tables = false;
                }
                result = "<p>Table List<br/>" + sbTables.toString() + "</p>";
            } catch (AmazonServiceException e) {
                System.out.println("Service Exception: " + e.getErrorMessage() );
                return "Service Exception: " + e.getErrorMessage();
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
                return "Exception: " + e.getMessage();
            }
        }
        
        return result;
    }

    public String deleteItem (String year, String title) {
        final String tableName  = "Movies";
        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put("year", new AttributeValue().withN(year));
        key_to_get.put("title", new AttributeValue(title));

        try {
            DeleteItemResult result =  dynamoDB.deleteItem(tableName, key_to_get).withConsumedCapacity(new ConsumedCapacity());
            System.out.println("Item Deleted. ConsumedCapacity: " + result.getConsumedCapacity());
            return "Item Deleted. ConsumedCapacity: " + result.getConsumedCapacity();
        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
            return "Delete error: " + e.getErrorMessage();
        }
    }

    public String  scanItem(String year, String title) {
        System.out.println("Scan Item Started");

        final String tableName = "Movies";
        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        AttributeValue attributeValuePK = new AttributeValue();
        attributeValuePK.setN(year);

        key_to_get.put("year", attributeValuePK);
        key_to_get.put("title", new AttributeValue(title));

        System.out.println("key: " + key_to_get);

        GetItemRequest request = new GetItemRequest()
                .withKey(key_to_get)
                .withTableName(tableName)
                .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        try {
            GetItemResult res1 =
                    dynamoDB.getItem(request);

            Map<String, AttributeValue> returned_item = res1.getItem();

            System.out.println("returned: " + res1.getConsumedCapacity());

            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                for (String key : keys) {
                    System.out.printf("%s: %s\n",
                            key, returned_item.get(key).toString());
                }
            } else {
                System.out.println("No value found for key ");
            }

        } catch (AmazonServiceException e) {
            System.err.println("error: " + e.getErrorMessage());
        }

        return "done";
    }

    public String  productItemAdd(JsonObject requestBody) {
        System.out.println("ProductCatalog add Started");

        Random random = new Random();
        DynamoDB db = new DynamoDB(dynamoDB);
        final Table table = db.getTable("ProductCatalog");

        final Integer Id = requestBody.getInteger("Id") != null
                ? requestBody.getInteger("Id")
                : random.nextInt(1000);

// Build a list of related items
        List<Number> relatedItems = new ArrayList<Number>();
        relatedItems.add(341);
        relatedItems.add(472);
        relatedItems.add(649);

//Build a map of product pictures
        Map<String, String> pictures = new HashMap<String, String>();
        pictures.put("FrontView", "http://example.com/products/123_front.jpg");
        pictures.put("RearView", "http://example.com/products/123_rear.jpg");
        pictures.put("SideView", "http://example.com/products/123_left_side.jpg");

//Build a map of product reviews
        Map<String, List<String>> reviews = new HashMap<String, List<String>>();

        List<String> fiveStarReviews = new ArrayList<String>();
        fiveStarReviews.add("Excellent! Can't recommend it highly enough!  Buy it!");
        fiveStarReviews.add("Do yourself a favor and buy this");
        reviews.put("FiveStar", fiveStarReviews);

        List<String> oneStarReviews = new ArrayList<String>();
        oneStarReviews.add("Terrible product!  Do not buy this.");
        reviews.put("OneStar", oneStarReviews);

        final String stringId = Integer.toString(Id);
        Item item = new Item()
                .withPrimaryKey("Id", Id)
                .withString("Title", "Bicycle " + stringId)
                .withString("Description", stringId + " description")
                .withString("BicycleType", "Hybrid")
                .withString("Brand", "Brand-Company C")
                .withNumber("Price", 500)
                .withStringSet("Color",  new HashSet<String>(Arrays.asList("Red", "Black")))
                .withString("ProductCategory", "Bicycle")
                .withString("ISBN", "555-12345678")
                .withBoolean("InStock", true)
                .withNull("QuantityOnHand")
                .withList("RelatedItems", relatedItems)
                .withMap("Pictures", pictures)
                .withMap("Reviews", reviews);


        System.out.println( "Write the item to the table");
        System.out.println(item.toJSONPretty());

        PutItemOutcome outcome = table.putItem(item);

        return "Item " + stringId + " added: " + outcome.toString();
    }

    public String  productGetItem(JsonObject requestBody) {
        System.out.println("Product Catalog Get Item Started");

        Integer Id = requestBody.getInteger("Id") == null ? 0 : requestBody.getInteger("Id");

        DynamoDB db = new DynamoDB(dynamoDB);
        Table table = db.getTable("ProductCatalog");

        Item item = table.getItem("Id", Id);
        System.out.println(item.toJSONPretty());

        try {
            JsonObject jObj = new JsonObject(item.toJSON());
            System.out.println("ItemId: " + jObj.getInteger("Id"));
            System.out.println("Description: " + jObj.getString("Description"));
            System.out.println("Price: " + jObj.getInteger("Price"));
        }
        catch (Exception e) {
            System.out.println("Json Exception " + e.getMessage());
        }

        return item.toJSONPretty();
    }

    public String  productDeleteItem(JsonObject requestBody) {
        System.out.println("Product Catalog Delete Item Started");

        Integer Id = requestBody.getInteger("Id") == null ? 0 : requestBody.getInteger("Id");

        DynamoDB db = new DynamoDB(dynamoDB);
        Table table = db.getTable("ProductCatalog");

        DeleteItemOutcome outcome = table.deleteItem("Id", Id);
        System.out.println(outcome.toString());
        return "Item " + Integer.toString(Id) + " deleted: " + outcome.toString();
    }

    public String deleteTable(String tableName) {
        System.out.println("Delete table " + tableName);

        try {
            DeleteTableResult result = dynamoDB.deleteTable(tableName);
            System.out.println("Delete table " + tableName);
            return "Table deleted " + result.toString();
        } catch (Exception e) {
            System.out.println("Delete table failed " + e.getMessage());
            return "Delete table failed " + e.getMessage();
        }
    }

    private RequestFields parseRequest(JsonObject requestBody){
        RequestFields requestFields = new RequestFields();

        return requestFields;
    }

//    public String deleteTable(String tableName) {
//        System.out.println("Delete table " + tableName);
//
//        try {
//            DynamoDB db = new DynamoDB(dynamoDB);
//            Table table = db.getTable(tableName);
//            DeleteTableResult result = table.delete();
//            table.waitForDelete();
//            System.out.println("Delete table " + tableName);
//            return "Table deleted " + result.toString();
//        } catch (Exception e) {
//            System.out.println("Delete table failed " + e.getMessage());
//            return "Delete table failed " + e.getMessage();
//        }
//    }
}
