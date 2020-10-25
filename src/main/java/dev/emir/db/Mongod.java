package dev.emir.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

public class Mongod {

    private MongoClient client;
    private MongoDatabase database;

    public Mongod() {
        client = MongoClients.create("mongodb+srv://gh_own:tm1Jyqi1SnejYedj@gameshidden.nqdla.azure.mongodb.net/admin?retryWrites=true&w=majority");
        database = client.getDatabase("HiddenKiller");
    }

    public void disconnect() {
        client.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collection) {
        return database.getCollection(collection);
    }

    public void replace(String collection, String from, String identifier, Document document) {
        getCollection(collection).replaceOne(Filters.eq(from, identifier), document, new ReplaceOptions().upsert(true));
    }

    public void replace(MongoCollection<Document> collection, String from, String identifier, Document document) {
        collection.replaceOne(Filters.eq(from, identifier), document, new ReplaceOptions().upsert(true));
    }

}
