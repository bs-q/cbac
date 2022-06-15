package hq.remview.data.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.obj.RestaurantSetting;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity(tableName = "db_restaurant")
public class RestaurantEntity extends BaseEntity {
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @NonNull
    public String id;

    @ColumnInfo(name = "pos_id")
    public String posId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "last_access_date")
    public Date lastAccessDate;

    @ColumnInfo(name = "token")
    public String token;

    @ColumnInfo(name = "setting")
    public String setting;

    @Ignore
    public boolean active = false;

    public RestaurantEntity(){}

    public RestaurantSetting getSetting(){
        if(setting!=null){
            RestaurantSetting setting = ApiModelUtils.fromJson(this.setting,RestaurantSetting.class);
            return setting;
        }
        return null;
    }
}
