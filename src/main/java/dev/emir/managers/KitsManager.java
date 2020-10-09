package dev.emir.managers;

import dev.emir.models.KitModel;
import dev.emir.models.ManagerModel;

public class KitsManager extends ManagerModel<KitModel> {

    @Override
    public KitModel get(String identifier) {
        for (KitModel model : this.getList()) {
            if (model.getName().equals(identifier)) return model;
        }
        return null;
    }
}
