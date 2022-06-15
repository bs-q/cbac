package hq.remview.data.model.api.response.permission;

import androidx.annotation.NonNull;

import java.util.List;

import lombok.Data;

@Data
public class PermissionResponse {
    private List<Permission> datas;
    @Data
    public static class Permission implements Cloneable {
        private String name;
        private String password;
        private Integer role;
        private String ibutton;
        private String nfc;

        @NonNull
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
