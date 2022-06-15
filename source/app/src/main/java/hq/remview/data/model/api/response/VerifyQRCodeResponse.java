package hq.remview.data.model.api.response;

import lombok.Data;

@Data
public class VerifyQRCodeResponse extends BaseSocketResponse{
    private String enabledRemview;
    private String token;
    private String posName;
    private String posId;
    private boolean isActive;
}
