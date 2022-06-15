package hq.remview.data.model.api.obj;

import java.io.Serializable;

import lombok.Data;

@Data
public class BillingItemUnit implements Serializable {
    private String date;
    private Integer billNumber;
    private String employee;
    private String tableName;
    private Integer totalMoney;
    private String paymentMethod;
    private String inOrOut;
    private Double percent;
    private String isBillingDelete;
    private String fromPC;
    private String monAn;
}
