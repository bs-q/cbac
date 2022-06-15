package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class SocketVerifyQRCode {
    private String code;
    private String deviceId;
}
