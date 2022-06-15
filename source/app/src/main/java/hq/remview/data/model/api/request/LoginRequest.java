package hq.remview.data.model.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class LoginRequest extends BaseRequest{
    private String username;
    private String password;
}
