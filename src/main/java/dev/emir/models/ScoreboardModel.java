package dev.emir.models;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.Expose;
import dev.emir.interfaces.IConfigurationModel;
import dev.emir.objects.LobbyScoreboard;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class ScoreboardModel implements IConfigurationModel<ScoreboardModel> {

    @Expose(serialize = false, deserialize = false)
    private ArrayList<ScoreboardText> list;
    @Expose(serialize = false, deserialize = false)
    private Scoreboard scoreBoard;
    @Expose(serialize = false, deserialize = false)
    private Objective objective;
    @Expose(serialize = false, deserialize = false)
    private String tag;
    @Expose(serialize = false, deserialize = false)
    private int lastSentCount;
    @Expose(serialize = false, deserialize = false)
    private PlayerModel target;

    public ScoreboardModel(Scoreboard score, String title) {
        this.list = new ArrayList();
        this.tag = "PlaceHolder";
        this.lastSentCount = -1;
        Preconditions.checkState(title.length() <= 32, "title can not be more than 32");
        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.scoreBoard = score;
        (this.objective = getOrCreateObjective(this.tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void add(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        ScoreboardText text = null;
        if (input.length() <= 16) {
            text = new ScoreboardText(input, "");
        } else {
            String first = input.substring(0, 16);
            String second = input.substring(16, input.length());
            if (first.endsWith(String.valueOf(65533))) {
                first = first.substring(0, first.length() - 1);
                second = String.valueOf(String.valueOf(65533)) + second;
            }
            String lastColors = ChatColor.getLastColors(first);
            second = String.valueOf(String.valueOf(lastColors)) + second;
            text = new ScoreboardText(first, StringUtils.left(second, 16));
        }
        this.list.add(text);
    }

    public void clear() {
        this.list.clear();
    }

    public void remove(int index) {
        String name = getNameForIndex(index);
        this.scoreBoard.resetScores(name);
        Team team = getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + index, index);
        team.unregister();
    }
    public void sendBoard() {}
    public void update() {
        target.getPlayer().setScoreboard(this.scoreBoard);
        for (int sentCount = 0; sentCount < this.list.size(); sentCount++) {
            Team i = getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + sentCount, sentCount);
            ScoreboardText str = this.list.get(this.list.size() - sentCount - 1);
            i.setPrefix(str.getLeft());
            i.setSuffix(str.getRight());
            this.objective.getScore(getNameForIndex(sentCount)).setScore(sentCount + 1);
        }
        if (this.lastSentCount != -1) {
            int sentCount = this.list.size();
            for (int var4 = 0; var4 < this.lastSentCount - sentCount; var4++) {
                remove(sentCount + var4);
            }
        }
        this.lastSentCount = this.list.size();
    }

    public Team getOrCreateTeam(String team, int i) {
        Team value = this.scoreBoard.getTeam(team);
        if (value == null) {
            value = this.scoreBoard.registerNewTeam(team);
            value.addEntry(getNameForIndex(i));
        }
        return value;
    }

    public Objective getOrCreateObjective(String objective) {
        Objective value = this.scoreBoard.getObjective("dummyhubobj");
        if (value == null) {
            value = this.scoreBoard.registerNewObjective("dummyhubobj", "dummy");
        }
        value.setDisplayName(objective);
        return value;
    }

    public Objective getOrCreateObjective(String objective, String type) {
        Objective value = this.scoreBoard.getObjective(objective);
        if (value == null) {
            value = this.scoreBoard.registerNewObjective(objective, type);
        }
        value.setDisplayName(objective);
        return value;
    }

    public String getNameForIndex(int index) {
        return String.valueOf(String.valueOf(ChatColor.values()[index].toString())) + ChatColor.RESET;
    }

    public void setTitle(String title) {
        this.objective.setDisplayName(title.replace("&", "§"));
    }

    public Scoreboard getScoreBoard() {
        return scoreBoard;
    }

    public PlayerModel getTarget() {
        return target;
    }

    public void setTarget(PlayerModel target) {
        this.target = target;
    }


}
