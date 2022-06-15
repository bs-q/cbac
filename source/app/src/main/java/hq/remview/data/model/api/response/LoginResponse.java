package hq.remview.data.model.api.response;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class LoginResponse extends BaseResponse{
    private String username;
    private String token;
    private String fullName;
    private long id;
    private Date expired;
    private Integer kind;
}
