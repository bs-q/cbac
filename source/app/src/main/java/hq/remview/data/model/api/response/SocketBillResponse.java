package hq.remview.data.model.api.response;

import java.util.List;

import hq.remview.data.model.api.obj.BillingItemUnit;
import lombok.Data;

@Data
public class SocketBillResponse {
    private List<BillingItemUnit> datas;
}
