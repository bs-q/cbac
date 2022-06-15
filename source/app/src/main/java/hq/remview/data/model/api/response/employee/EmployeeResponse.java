package hq.remview.data.model.api.response.employee;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EmployeeResponse {
    public List<EmployeObject> employees = new ArrayList<>();

    public static class EmployeObject {
        public String employeeName;
        public List<Money> money = new ArrayList<>();
    }
    @Data
    public static class Money{
        private String name;
        private Integer money;
    }
}


