package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class VerifyTokenRequest extends BaseRequest{
    private String token;
}
