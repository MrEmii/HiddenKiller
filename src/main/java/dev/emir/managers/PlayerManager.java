package dev.emir.managers;

import com.mongodb.client.MongoCollection;
import dev.emir.Main;
import dev.emir.models.PlayerModel;
import dev.emir.utils.FileReader;
import org.bson.Document;
import org.bukkit.Bukkit;

import static com.mongodb.client.model.Filters.*;

public class PlayerManager {

    MongoCollection<Document> users;

    public PlayerManager(MongoCollection<Document> users) {
        this.users = users;
    }

    public void update() {
        this.users = Main.getInstance().getMongod().getCollection("hiddenkiller");
    }

    public boolean add(PlayerModel model) {
        return false;
    }

    public PlayerModel get(String identifier) {
        if (users.find(eq("uuid", identifier)).first() == null) {
            PlayerModel model = new PlayerModel().setUUID(identifier).setPlayer(Bukkit.getPlayer(identifier));
            return model;
        } else {
            return FileReader.gson.fromJson(((Document) users.find(eq("uuid", identifier)).first()).toJson(), PlayerModel.class);
        }

    }

    public PlayerModel getPlayerByName(String identifier) {
        if (users.find(eq("username", identifier)).first() == null) {
            PlayerModel model = new PlayerModel().setPlayer(Bukkit.getPlayer(identifier));
            return model;
        } else {
            return FileReader.gson.fromJson(((Document) users.find(eq("username", identifier)).first()).toJson(), PlayerModel.class);
        }
    }

}
