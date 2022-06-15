package hq.remview.data.model.api.response;

import java.util.List;

import hq.remview.data.model.api.obj.LogFoodItem;
import lombok.Data;

@Data
public class SocketLogFoodResponse {
    private List<LogFoodItem> datas;
}
