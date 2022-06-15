package hq.remview.ui.main.setting.permission;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.BR;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.ActivityPermissionBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.revenue.sell.detail.adapter.SellDetailAdapter;
import hq.remview.ui.main.setting.permission.adapter.PermissionAdapter;
import hq.remview.ui.main.setting.permission.edit.PermissionEditActivity;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PermissionActivity extends BaseActivity<ActivityPermissionBinding,PermissionViewModel>
implements PermissionAdapter.PermissionClick {
    @Override
    public int getLayoutId() {
        return R.layout.activity_permission;
    }
    public static PermissionResponse permissionResponse;
    private PermissionAdapter adapter;
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        adapter = new PermissionAdapter();
        adapter.permissionResponse = permissionResponse;
        adapter.callback = this;
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void PermissionClick(PermissionResponse.Permission permission) {
        navigateToPermissionEdit(permission);
    }
    private void navigateToPermissionEdit(PermissionResponse.Permission permission){
        Intent it = new Intent(this, PermissionEditActivity.class);
        try {
            PermissionEditActivity.permission = (PermissionResponse.Permission) permission.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        startActivity(it);
    }
    @Override
    public void onMessage(SocketEventModel socketEventModel){
        runOnUiThread(() -> handleMessage(socketEventModel));
    }

    private void handleMessage(SocketEventModel socketEventModel){
        if(socketEventModel.getMessage().getResponseCode() == 200){
            if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals("PERMISSION_LIST")) {
                Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                return;
            }
            switch (socketEventModel.getMessage().getCmd()){
                case Command.LAY_DANH_SACH_NHANVIEN_UPDATE:
                    updateList(socketEventModel.getMessage().getData().toString());
                    viewModel.hideLoading();
                    break;
                default:
                    break;
            }
        }else{
            viewModel.hideLoading();
        }
    }
    private void updateList(String json){
        PermissionActivity.permissionResponse = ApiModelUtils.fromJson(json,PermissionResponse.class);
        adapter.permissionResponse = permissionResponse;
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        employeePermission();
    }

    public void employeePermission(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen("PERMISSION_LIST");
        msg.setCmd(Command.LAY_DANH_SACH_NHANVIEN_UPDATE);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionResponse = null;
    }
}
