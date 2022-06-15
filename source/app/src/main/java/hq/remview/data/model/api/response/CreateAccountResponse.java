package hq.remview.data.model.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateAccountResponse extends BaseResponse {
    private String email;
    private Integer id;
    private String username;
}
