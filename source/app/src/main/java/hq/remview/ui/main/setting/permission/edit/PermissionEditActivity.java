package hq.remview.ui.main.setting.permission.edit;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.BR;
import hq.remview.data.model.api.request.SocketRevenueRequest;
import hq.remview.data.model.api.request.UpdatePermissionRequest;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.ActivityEditPermissionBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.setting.permission.PermissionActivity;
import hq.remview.ui.main.setting.permission.adapter.PermissionAdapter;
import hq.remview.ui.main.setting.permission.edit.adapter.PermissionEditAdapter;
import timber.log.Timber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class PermissionEditActivity extends BaseActivity<ActivityEditPermissionBinding,PermissionEditViewModel>
implements View.OnClickListener {
    public static PermissionResponse.Permission permission;
    private PermissionEditAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_permission;
    }

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
        adapter = new PermissionEditAdapter();
        adapter.createPermission(permission.getRole());
        adapter.createPermissionList();
        setupEmployee();
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.recyclerView.setAdapter(adapter);
    }
    private void setupEmployee(){
        viewModel.name.set(permission.getName());
//        viewModel.password.set(permission.getPassword());
        viewModel.nfc.set(permission.getNfc());
        if (permission.getIbutton() == null || permission.getIbutton().isEmpty()) {
            viewModel.ibutton.set(false);
        }else {
            viewModel.ibutton.set(true);
        }
    }
    private void navigateToPermission(){
        Intent it = new Intent(this, PermissionActivity.class);
        startActivity(it);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibutton) {
            updateIbutton();
        } else if (v.getId() == R.id.save_btn) {
            updatePermission();
        }
    }

    @Override
    public void onMessage(SocketEventModel socketEventModel){
        runOnUiThread(() -> handleMessage(socketEventModel));
    }
    private boolean mode; // true: update employee, false: update ibutton
    private void handleMessage(SocketEventModel socketEventModel){
        if(socketEventModel.getMessage().getResponseCode() == 200){
            if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals("PERMISSION")) {
                Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                return;
            }
            switch (socketEventModel.getMessage().getCmd()){
                case Command.UPDATE_NHAN_VIEN:
                    viewModel.hideLoading();
                    viewModel.showSuccessMessage(getString(R.string.updatepermissionmsg));
                    navigateToPermission();
                    Timber.d("Update employee");
                    break;
                default:
                    break;
            }
        }else{
            viewModel.hideLoading();
        }
    }
    private int updateRole(){
        return adapter.permissionToggleItemList.stream().filter(o->o.check.get()).mapToInt(o->o.base).sum();
    }
    public void updatePermission(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen("PERMISSION");
        msg.setCmd(Command.UPDATE_NHAN_VIEN);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().getToken());
        UpdatePermissionRequest request = new UpdatePermissionRequest();
        request.setOldName(permission.getName());
        request.setEmployee(permission);
        if (viewModel.name.get().isEmpty()){
            request.getEmployee().setName(request.getOldName());
        } else {
            request.getEmployee().setName(viewModel.name.get());
        }
        if (!viewModel.password.get().isEmpty()) {
            request.getEmployee().setPassword(viewModel.password.get());
        }
        if (!viewModel.nfc.get().isEmpty()) {
            request.getEmployee().setNfc(viewModel.nfc.get());
        }
        request.getEmployee().setRole(updateRole());
        Timber.d(request.getEmployee().getRole().toString());
        msg.setData(request);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToPermission();
    }

    public void updateIbutton(){
        permission.setIbutton("");
        viewModel.ibutton.set(false);
    }

}
