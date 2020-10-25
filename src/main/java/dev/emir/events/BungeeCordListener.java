package dev.emir.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.emir.Main;
import dev.emir.enums.GameStates;
import dev.emir.models.GameInformation;
import dev.emir.models.GameModel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;

public class BungeeCordListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord") && !channel.equals("HiddenKiller")) {
            return;
        }
        try {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            final String subchannel = in.readUTF();
            if (subchannel.equalsIgnoreCase("gameinfo")) {
                String gameName = in.readUTF();
                GameModel model = Main.getInstance().getGameManager().get(gameName);
                if (model != null) {
                    final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("gameinfo");
                    out.writeUTF(Main.gson.toJson(new GameInformation(gameName, model.getPlayers().size(), model.getMaxPlayers(), model.getGameState().toString())));
                    Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "HiddenKiller", out.toByteArray());
                }
            }
            if (subchannel.equals("gameadd")) {
                String serverName = in.readUTF();
                String gameName = in.readUTF();
                String playerUUID = in.readUTF();
                System.out.println(gameName);
                System.out.println(serverName);
                System.out.println(playerUUID);

                final ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("gameinfo");
                out.writeUTF(serverName);
                out.writeUTF(Main.gson.toJson(new GameInformation(gameName, 4, 12, GameStates.STARTED.name())));
                Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "HiddenKiller", out.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGame(GameModel model) {
        try {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("gameupdate");
            out.writeUTF(Main.gson.toJson(new GameInformation(model.getGameName(), model.getPlayers().size(), model.getMaxPlayers(), model.getGameState().toString())));
            Main.getInstance().getServer().sendPluginMessage(Main.getInstance(), "HiddenKiller", out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String server, Player player) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("Connect");
                out.writeUTF(server);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            player.sendPluginMessage(Main.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
