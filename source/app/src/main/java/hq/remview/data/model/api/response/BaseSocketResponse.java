package hq.remview.data.model.api.response;

import lombok.Data;

@Data
public class BaseSocketResponse extends BaseResponse{
    private Integer responseCode;
    private String msg;
}
