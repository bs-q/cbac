package hq.remview.data.model.api;

import java.util.List;

import lombok.Data;

@Data
public class ResponseListObj<T> {
    private List<T> data;
    private Integer page;
    private Integer totalPage;
    private Long totalElements;
    private Integer oldIndex;

    public boolean hasNext() {
        return totalPage - 1 > page;
    }

    public Integer getNext() {
        return page + 1;
    }

    public void copy(ResponseListObj<T> clone) {
        this.page = clone.page;
        this.totalPage = clone.totalPage;
        this.totalElements = clone.totalElements;
        this.oldIndex = clone.data.size() - 1;
        this.data.addAll(clone.data);
    }
    public void clear(){
        this.totalPage = 0;
        this.page = 0;
    }
}
