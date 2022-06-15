package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class SocketReportRequest {
    private String fromDate;
    private String toDate;
    private String pos;
}
