package com.highland.leads.cosmos;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.highland.leads.models.JobRequest;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    private final static String HOST = "https://highland-cosmosdb.documents.azure.com:443/";
    private final static String MASTER_KEY = "JmWMMJNVtOalGtM0RG71e9KMBg1bx0KK2Esf7h9E3Le8R2yLZgEL1rKbVeS9lwmftOXk2BIUUcjCVC28ZmBuuw==";
    private static String databaseName = "JOB_DATABASE";
    private static String containerName = "JOB_CONTAINER";
    private static String partitionKeyPath = "/id";
    private static CosmosClient cosmosClient = null;
    private static CosmosContainer cosmosContainer = null;
    private static ObjectMapper objectMapper;

    public static void initialize(){
        if(cosmosClient == null) {
            cosmosClient = new CosmosClientBuilder()
                    .endpoint(HOST)
                    .key(MASTER_KEY)
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .buildClient();
        }
        CosmosDatabaseResponse cosmosDatabaseResponse = cosmosClient.createDatabaseIfNotExists(databaseName);
        CosmosDatabase database = cosmosClient.getDatabase(cosmosDatabaseResponse.getProperties().getId());
        CosmosContainerProperties containerProperties = new CosmosContainerProperties(containerName, partitionKeyPath);
        CosmosContainerResponse cosmosContainerResponse = database.createContainerIfNotExists(containerProperties, ThroughputProperties.createManualThroughput(400));
        cosmosContainer = database.getContainer(cosmosContainerResponse.getProperties().getId());
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }
    public static void createJobRequest(JobRequest jobRequest){
        if(cosmosContainer == null) initialize();
        try{
            CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
            JsonNode jobRequestJson = objectMapper.valueToTree(jobRequest);
            System.out.println(jobRequestJson.toString());
            cosmosContainer.createItem(jobRequestJson, new PartitionKey(jobRequest.getId()), cosmosItemRequestOptions);
        }catch (CosmosException e){
            e.printStackTrace();
        }
    }
    public static JobRequest getJobRequest(String id){
        if(cosmosContainer == null) initialize();
        try {
            CosmosItemResponse<JobRequest> item = cosmosContainer.readItem(id, new PartitionKey(id), JobRequest.class);
            return item.getItem();
        } catch (CosmosException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean isValidId(String id){
        if(cosmosContainer == null) initialize();
        return getDocumentById(id) == null;
    }
    public static List<JobRequest> getAllJobRequests(){
        if(cosmosContainer == null) initialize();
        try {
            CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
            //queryOptions.setEnableCrossPartitionQuery(true); //No longer necessary in SDK v4
            //  Set query metrics enabled to get metrics around query executions
            queryOptions.setQueryMetricsEnabled(true);

            CosmosPagedIterable<JobRequest> jobRequestCosmosPagedIterable = cosmosContainer.queryItems("SELECT * FROM JOB", queryOptions,JobRequest.class);
            return jobRequestCosmosPagedIterable.stream().collect(Collectors.toList());
        } catch (CosmosException e) {
            e.printStackTrace();
            return List.of();
        }
    }
    private static JsonNode getDocumentById(String id) {
        if(cosmosContainer == null) initialize();
        String sql = "SELECT * FROM JOB j WHERE j.id='" + id + "'";
        int maxItemCount = 1000;
        int maxDegreeOfParallelism = 1000;
        int maxBufferedItemCount = 100;

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setMaxBufferedItemCount(maxBufferedItemCount);
        options.setMaxDegreeOfParallelism(maxDegreeOfParallelism);
        options.setQueryMetricsEnabled(false);

        List<JsonNode> itemList = new ArrayList<>();

        String continuationToken = null;
        do {
            for (FeedResponse<JsonNode> pageResponse :
                    cosmosContainer
                            .queryItems(sql, options, JsonNode.class)
                            .iterableByPage(continuationToken, maxItemCount)) {

                continuationToken = pageResponse.getContinuationToken();

                for (JsonNode item : pageResponse.getElements()) {
                    itemList.add(item);
                }
            }

        } while (continuationToken != null);

        try{
            if (itemList.size() > 1) {
                throw new Exception("Multiple entries have the same ID");
            }else if (!itemList.isEmpty()){
                return itemList.get(0);
            }else{
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }
    public static void updateJobRequest(JobRequest jobRequest) {
        if(cosmosContainer == null) initialize();

        // Retrieve the document from the database
        //JsonNode oldJobRequestJson = getDocumentById(jobRequest.getId());

        // You can update the document as a JSON document directly.
        // For more complex operations - you could de-serialize the document in
        // to a POJO, update the POJO, and then re-serialize the POJO back in to
        // a document.

        //assert oldJobRequestJson != null;
        /*JobRequest tempJobRequest;
        try{
            tempJobRequest = objectMapper.treeToValue(oldJobRequestJson, JobRequest.class);
            if(tempJobRequest == null) throw new NullPointerException();
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        tempJobRequest.setId(jobRequest.getId());
        tempJobRequest.setSelectedStartDate(jobRequest.getSelectedStartDate());
        tempJobRequest.setSelectedEndDate(jobRequest.getSelectedEndDate());
        tempJobRequest.setStartTime(jobRequest.getStartTime());
        tempJobRequest.setEndTime(jobRequest.getEndTime());
        tempJobRequest.setMortgageMinimum(jobRequest.getMortgageMinimum());
        tempJobRequest.setMortgageMaximum(jobRequest.getMortgageMaximum());
        tempJobRequest.setStatus(jobRequest.getStatus());
        tempJobRequest.setFile(jobRequest.getFile());*/
        try{
            JsonNode newJson = objectMapper.valueToTree(jobRequest);
            cosmosContainer
                    .replaceItem(newJson, jobRequest.getId(), new PartitionKey(jobRequest.getId()), new CosmosItemRequestOptions());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
