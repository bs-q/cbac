package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class InReportRequest {
    private String fromDate;
    private String toDate;
    private String month;
    private String pos;
}
