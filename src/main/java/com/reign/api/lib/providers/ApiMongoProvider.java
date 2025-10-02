package com.reign.api.lib.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.ApiResponse;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ApiMongoProvider implements IApiProvider {
    private static final Logger log = LoggerFactory.getLogger(ApiMongoProvider.class);

    static final String COLLECTION_NAME = "guilds";

    MongoClient client;
    MongoDatabase db;
    MongoCollection<Document> collection;

    public ApiMongoProvider(String hostUri, String dbName) {
        try {
            client = MongoClients.create(hostUri);
            db = client.getDatabase(dbName);
            collection = db.getCollection(COLLECTION_NAME);

            log.debug("Connected to mongodb host, can see {} documents.", collection.countDocuments());

        } catch (MongoException e) {
            log.error("Failed to connect to MongoDB host.", e);
        }
    }

    @Override
    public <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass) {
        try {
            // Convert the ApiModel to a JSON-like Document for MongoDB storage
            ObjectMapper objectMapper = new ObjectMapper();
            Document document = Document.parse(objectMapper.writeValueAsString(apiModel));

            String guildId = endpoint.substring(endpoint.lastIndexOf("/") + 1);

            // Perform an upsert operation (insert if not exists, update otherwise)
            collection.updateOne(
                    eq("snowflake", guildId),
                    new Document("$set", document),
                    new com.mongodb.client.model.UpdateOptions().upsert(true)
            );

            // Fetch the updated document from MongoDB
            Document updatedDocument = collection.find(eq("snowflake", guildId)).first();
            if (updatedDocument == null) {
                log.error("Failed to fetch updated document for guild ID: {}", guildId);
                return false;
            }

            // Convert the updated document into an instance of Y (ApiResponse<?>)
            Y response = objectMapper.readValue(updatedDocument.toJson(), responseClass);

            // Ensure the operation was successful (assuming status field exists)
            return response.status == 200;
        } catch (Exception e) {
            log.error("Error committing API model to endpoint: {}", endpoint, e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass) {
        String guildId = endpoint.substring(endpoint.lastIndexOf("/") + 1);
        log.debug("Fetching guild with ID: {}", guildId);

        try {
            // Create response object
            ApiResponse<ApiGuild> response = (ApiResponse<ApiGuild>) responseClass.getDeclaredConstructor().newInstance();

            // Ensure data field is initialized
            if (response.data == null) {
                response.data = new ArrayList<>();
            }

            // Fetch documents from MongoDB
            List<Document> documents = collection.find(eq("snowflake", guildId)).into(new ArrayList<>());

            // Convert MongoDB documents to ApiGuild objects using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            for (Document doc : documents) {
                ApiGuild guild = objectMapper.readValue(doc.toJson(), ApiGuild.class);
                response.data.add(guild);
                log.debug(guild.toString());
            }

            response.status = 200;
            return (Y) response;
        } catch (Exception e) {
            log.error("Failed to fetch guild data for guild ID: {}", guildId, e);
            throw new RuntimeException(e);
        }
    }
}
