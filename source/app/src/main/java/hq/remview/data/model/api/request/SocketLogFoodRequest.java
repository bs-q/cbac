package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class SocketLogFoodRequest {
    private String fromDate;
    private String toDate;
    private String fromPc;
}
