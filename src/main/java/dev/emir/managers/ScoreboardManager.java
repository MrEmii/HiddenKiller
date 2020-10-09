package dev.emir.managers;

import dev.emir.models.ManagerModel;
import dev.emir.models.PlayerModel;
import dev.emir.models.ScoreboardModel;

public class ScoreboardManager extends ManagerModel<ScoreboardModel> {

    @Override
    public ScoreboardModel get(String identifier) {
        for (ScoreboardModel model : this.getList()) {
            if (model.getTarget().getPlayer().getDisplayName().equals(identifier)) return model;
        }
        return null;
    }

    public ScoreboardModel get(PlayerModel identifier, ScoreboardModel newModel) {
        ScoreboardModel model = get(identifier.getPlayer().getDisplayName());
        if (model == null) {
            newModel.save();
            this.add(newModel);
            return get(identifier.getPlayer().getDisplayName());
        } else {
            return model;
        }
    }
}
