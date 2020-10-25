package dev.emir.managers;

import com.mongodb.client.MongoCollection;
import dev.emir.Main;
import dev.emir.models.PlayerModel;
import org.bson.Document;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class PlayerManager {

    MongoCollection<Document> users;
    HashMap<String, PlayerModel> models;

    public PlayerManager(MongoCollection<Document> users) {
        this.users = users;
        this.models = new HashMap<>();
    }

    public PlayerModel get(String identifier) {
        if (users.find(eq("uuid", identifier)).first() == null || !models.containsKey(identifier)) {
            PlayerModel model = new PlayerModel().setUuid(identifier);
            model.save();
            this.models.put(identifier, model);
            return model;
        } else {
            if (!this.models.containsKey(identifier)) {
                PlayerModel model = Main.gson.fromJson(((Document) users.find(eq("uuid", identifier)).first()).toJson(), PlayerModel.class);
                this.models.put(identifier, model);
            }
            return this.models.get(identifier);
        }
    }

}
