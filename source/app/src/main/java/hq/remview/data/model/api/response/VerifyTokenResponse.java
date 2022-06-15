package hq.remview.data.model.api.response;


import lombok.Data;

@Data
public class VerifyTokenResponse {
    private String enabledRemview;
    private String token;
}
