package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class NavigationRequest extends BaseRequest{
    private String path;
}
