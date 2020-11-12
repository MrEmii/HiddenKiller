package dev.emir.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.emir.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class BungeeCoordEvents implements PluginMessageListener {

    private final HashMap<String, Integer> servers;

    public BungeeCoordEvents() {
        this.servers = new HashMap<>();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (subchannel.equals("PlayerCount")) {
                String server = in.readUTF();
                int playercount = in.readInt();
                Main.getInstance().getBungeeCoord().servers.put(server, playercount);
            }
        }
    }

    public void connect(String server, Player player) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public void playerCount(String server) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("PlayerCount");
            out.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
    }

    public HashMap<String, Integer> getServers() {
        return servers;
    }
}
