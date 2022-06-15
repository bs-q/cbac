package hq.remview.ui.main.setting.employee;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;
import java.util.Objects;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.request.EmployeeMoneyRequest;
import hq.remview.data.model.api.response.employee.EmployeeResponse;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.ActivityEmployeeBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.GridSpacingItemDecoration;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.setting.employee.adapter.EmployeeAdapter;
import hq.remview.ui.main.setting.employee.detail.EmployeeDetailActivity;
import lombok.Data;
import timber.log.Timber;

public class EmployeeActivity extends BaseActivity<ActivityEmployeeBinding,EmployeeViewModel> implements EmployeeAdapter.EmployeeAdapterCallback {


    @Override
    public void detail(EmployeeResponse.EmployeObject employeeObject) {
        navigateToEmployeeDetail(employeeObject.employeeName,employeeObject.money);
    }

    private void navigateToEmployeeDetail(String name, List<EmployeeResponse.Money> moneyList){
        Intent it = new Intent(this, EmployeeDetailActivity.class);
        it.putExtra(EmployeeDetailActivity.EMPLOYEE_NAME,name);
        needClearData = true;
        EmployeeDetailActivity.money = moneyList;
        startActivity(it);
    }
    @Data
    public static class EmployeeRequest{
        private String employee;
        private String pos;
    }

    EmployeeAdapter adapter;
    public static EmployeeResponse response;

    @Override
    public int getLayoutId() {
        return R.layout.activity_employee;
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
        adapter = new EmployeeAdapter();
        adapter.setResponse(response);
        adapter.setCallback(this);
        adapter.application = (MVVMApplication) getApplication();
        int spacing = getResources().getDimensionPixelSize(R.dimen._5sdp);
        viewBinding.rv.addItemDecoration(new GridSpacingItemDecoration(2,spacing,2*spacing,true));
        viewBinding.rv.setLayoutManager(new GridLayoutManager(this,2));
        viewBinding.rv.setAdapter(adapter);
        viewModel.tick.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Timber.d("tick");
                employeeMoney();
            }
        });
    }

    @Override
    protected void onDestroy() {
        response = null;
        super.onDestroy();
    }
    boolean needClearData = false;
    @Override
    protected void onResume() {
        super.onResume();
        if (needClearData){
            adapter.clearData();
            needClearData = false;
        }
        Timber.d("onResume");
        viewModel.startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("onPause");
        viewModel.disposeBag();
    }

    @Override
    public void onMessage(SocketEventModel socketEventModel) {
        runOnUiThread(() -> {
            handleSocket(socketEventModel);
        });
    }

    private String currentCMD;
    // handle
    public void handleSocket(SocketEventModel socketEventModel){
        if(Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_MESSAGE)){
            if(socketEventModel.getMessage().getResponseCode() == 200){
                Timber.d("Screen %s",socketEventModel.getMessage().getScreen());
                if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals("EMPLOYEE_ACTIVITY")) {
                    Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                    return;
                }
                switch (socketEventModel.getMessage().getCmd()){
                    case Command.LAY_DANH_SACH_NHAN_VIEN:
                        viewModel.startTimer();
                        Timber.d("Update employee money");
                        EmployeeResponse employeeResponse = ApiModelUtils.fromJson(socketEventModel.getMessage().getData().toString(),EmployeeResponse.class);
                        adapter.setResponse(employeeResponse);
                        adapter.notifyDataSetChanged();
                        break;

                    default:
                        break;
                }
            }else{
                //call request bi loi
                switch (currentCMD){
                    case Command.COMMAND_VERIFY_QRCODE:
                        viewModel.showErrorMessage(getString(R.string.qr_code_wrong_format));
                        viewModel.hideLoading();
                        break;
                    case Command.COMMAND_TOKEN_CLIENT:
                        viewModel.showErrorMessage(getString(R.string.session_timeout));
                        viewModel.hideLoading();
                        break;
                    default:
                        viewModel.hideLoading();
                        break;
                }
            }
            //jGEgwACeoLDpOG -> de

        } else if (Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_OFFLINE)
                || Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_ERROR)){
            viewModel.notifySocketError();
        }
    }

    public void employeeMoney(){
        Message msg = new Message();
        msg.setScreen("EMPLOYEE_ACTIVITY");
        msg.setCmd(Command.LAY_DANH_SACH_NHAN_VIEN);
        currentCMD = Command.LAY_DANH_SACH_NHAN_VIEN;
        EmployeeMoneyRequest request = new EmployeeMoneyRequest();
        request.setFromPc(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting().getFromPcs());
        msg.setData(request);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
}
