package dev.emir.events;

import dev.emir.Main;
import dev.emir.api.ColorText;
import dev.emir.models.GameModel;
import dev.emir.models.PlayerModel;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SignEvent implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("gh.op")) {
            if (event.getLine(0).equalsIgnoreCase("[game]")) {
                List<String> sign = Main.getInstance().getConfig().getStringList("sign");
                GameModel model = Main.getInstance().getGameManager().get(event.getLine(1));
                if (model != null) {
                    for (int i = 0; i < sign.size(); i++) {
                        String line = sign.get(i).replace("&", "§")
                                .replace("{game}", model.getGameName())
                                .replace("{state}", ColorText.translate(model.getGameState().getText()))
                                .replace("{spawns}", (model.getPlayers().size() + "/" + model.getMaxPlayers()));
                        event.setLine(i, line);
                    }
                    Main.getInstance().getSignManager().addSign(model.getGameName(), event.getBlock().getState().getLocation());
                }

            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        PlayerModel model = Main.getInstance().getPlayerManager().get(player.getUniqueId().toString());

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN || e.getClickedBlock().getType() == Material.SIGN_POST) {
                Sign signblock = (Sign) e.getClickedBlock().getState();
                if (Main.getInstance().getSignManager().exist(signblock.getLocation())) {
                    String game = Main.getInstance().getSignManager().getGame(signblock.getLocation());
                    GameModel gm = Main.getInstance().getGameManager().get(game);
                    if(gm != null){

                    }

                }
            }
        }

    }


}
