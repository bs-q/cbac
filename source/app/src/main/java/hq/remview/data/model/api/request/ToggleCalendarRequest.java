package hq.remview.data.model.api.request;

import lombok.Data;

@Data
public class ToggleCalendarRequest extends BaseRequest {
    private String show;
}
