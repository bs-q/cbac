package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class WebQRCodeRequest extends BaseRequest{
    String qrCode;
}
