package dev.emir.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Mongod {

    private MongoClient client;
    private MongoDatabase database;

    public Mongod() {
        client = MongoClients.create("mongodb+srv://gh_own:tm1Jyqi1SnejYedj@gameshidden.nqdla.azure.mongodb.net/admin?retryWrites=true&w=majority");
        database = client.getDatabase("GamesHidden");
    }

    public void disconnect(){
        client.close();
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection(String collection){
        return database.getCollection(collection);
    }
}
