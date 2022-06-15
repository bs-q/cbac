package hq.remview.data.model.api.response.news;

import java.util.Date;

import lombok.Data;

@Data
public class NewsDetailResponse {
    private String avatar;
    private String banner;
    private Long categoryId;
    private String content;
    private String createdBy;
    private Date createdDate;
    private String description;
    private Long id;
    private Long kind;
    private String modifiedBy;
    private Date modifiedDate;
    private Long pinTop;
    private Long status;
    private String title;
}
