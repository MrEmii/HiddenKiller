package dev.emir.commands;

import dev.emir.utils.ChatCreator;
import dev.emir.utils.command.Command;
import dev.emir.utils.command.CommandArgs;
import net.md_5.bungee.api.chat.TextComponent;

public class GameCommands {


    @Command(name = "hk", permission = "hk.admin", aliases = {"hiddenkiller", "killer"}, inGameOnly = true)
    public void hiddenKiller(CommandArgs commandArgs){
        String[] args = commandArgs.getArgs();

        if(args.length > 0){

        }else{

        }
    }

    public void helpMessage(){
        TextComponent[] components = new TextComponent[10];
        components[0] = new ChatCreator("&b/hk setlobby").onSuggest("&7Setea el lobby", "hk setlobby");
        components[1] = new ChatCreator("&b/hk games").onSuggest("&7Muestra todos los juegos disponibles", "hk games");
        components[2] = new ChatCreator("&b/hk kits").onSuggest("&7Muestra los kits disponibles", "hk kits");
        components[3] = new ChatCreator("&b/hk reload").onSuggest("&7Recarga la información", "hk reload");
        components[4] = new ChatCreator("&b/hk setlobby").onSuggest("&7Set main lobby", "hk setlobby");
        components[5] = new ChatCreator("&b/hk setlobby").onSuggest("&7Set main lobby", "hk setlobby");


    }

}
