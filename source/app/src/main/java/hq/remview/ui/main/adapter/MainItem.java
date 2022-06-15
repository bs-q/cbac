package hq.remview.ui.main.adapter;

import hq.remview.data.model.db.RestaurantEntity;
import lombok.Data;

@Data
public class MainItem {

    private RestaurantEntity entity;

    private boolean showMenu;

    public MainItem(RestaurantEntity entity){
        this.entity = entity;
        this.showMenu = false;
    }
}
