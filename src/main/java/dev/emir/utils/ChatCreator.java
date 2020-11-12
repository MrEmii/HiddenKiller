package dev.emir.utils;

import dev.emir.api.ColorText;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ChatCreator {

    private TextComponent component;

    public ChatCreator(String text) {
        this.component = new TextComponent(ColorText.translate(text));
    }

    public ChatCreator(TextComponent component) {
        this.component = component;
    }

    public TextComponent onHover(String hoverText) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText)));
        return component;
    }
    public TextComponent onSuggest(String hoverText, String command) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorText.translate(hoverText))));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return component;
    }

    public TextComponent onClick(String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return component;
    }

    public TextComponent onClick(String command, ClickEvent.Action action) {
        component.setClickEvent(new ClickEvent(action, command));
        return component;
    }

}
