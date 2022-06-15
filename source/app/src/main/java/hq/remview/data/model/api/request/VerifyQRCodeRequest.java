package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class VerifyQRCodeRequest extends BaseRequest{
    private String qrCode;
    private String deviceId;
}
