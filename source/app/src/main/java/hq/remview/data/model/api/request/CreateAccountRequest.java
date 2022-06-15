package hq.remview.data.model.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateAccountRequest extends BaseRequest {

    private String avatarPath;
    private String birthday;
    private Integer communeId;
    private Integer districtId;
    private String email;
    private String fullName;
    private String lang;
    private Integer organizeId;
    private String password;
    private String phone;
    private Integer provinceId;
    private String username;
}
