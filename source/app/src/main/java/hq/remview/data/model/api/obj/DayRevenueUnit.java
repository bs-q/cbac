package hq.remview.data.model.api.obj;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class DayRevenueUnit {
    private String name;
    @SerializedName("Summe")
    private Double totalMoney;
}
