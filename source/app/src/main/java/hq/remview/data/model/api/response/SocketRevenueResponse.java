package hq.remview.data.model.api.response;

import java.util.List;

import hq.remview.data.model.api.obj.DayRevenueUnit;
import lombok.Data;

@Data
public class SocketRevenueResponse {
    private List<DayRevenueUnit> dataReport;
}
