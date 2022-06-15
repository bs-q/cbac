package hq.remview.data.model.api.response.top;

import java.util.List;

import lombok.Data;

@Data
public class TopSaleResponse {
    private List<TopSaleItem> datas;
    @Data
    public static class TopSaleItem{
        private String name;
        private Integer value;
    }
}
