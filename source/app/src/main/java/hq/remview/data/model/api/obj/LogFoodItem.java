package hq.remview.data.model.api.obj;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class LogFoodItem {
    private String date;
    private String employee;// nguoi dat
    private String forEmployee;//nguoi huy
    private String tableName;
    private String foodName;
    private double quantity;
    private Integer price;
    private Integer totalMoney;
    private String printer;
    private String art;
    private String fromPC;

    public Date parseDate(){
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();

    }
}
