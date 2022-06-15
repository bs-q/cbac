package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class SocketRevenueRequest {
    public static final int REPORT_KIND_REVENUE_DAY = 13;
    public static final int REPORT_KIND_REVENUE_TOP_20 = 1;

    private String fromDate;
    private String toDate;
    private String fromPc;
    Integer kind;
}
