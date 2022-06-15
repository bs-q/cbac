package hq.remview.data.model.api.obj;

import lombok.Data;

@Data
public class FoodItem {
    public static final int FOOD_TYPE_FOOD = 0;
    public static final int FOOD_TYPE_BEILAGE = 1;

    private int foodType = FOOD_TYPE_FOOD;
    private String name;
    private Double price;
    private Integer amount;
}
