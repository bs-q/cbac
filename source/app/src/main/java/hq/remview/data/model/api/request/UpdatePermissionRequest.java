package hq.remview.data.model.api.request;

import hq.remview.data.model.api.response.employee.EmployeeResponse;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import lombok.Data;

@Data
public class UpdatePermissionRequest {
    private String oldName;
    private PermissionResponse.Permission employee;
}
