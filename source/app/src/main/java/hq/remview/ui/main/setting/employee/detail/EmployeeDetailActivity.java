package hq.remview.ui.main.setting.employee.detail;

import static hq.remview.ui.main.MainActivity.MAIN_SCREEN;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.obj.DayRevenueUnit;
import hq.remview.data.model.api.request.EmployeeMoneyRequest;
import hq.remview.data.model.api.response.SocketRevenueResponse;
import hq.remview.data.model.api.response.employee.EmployeeResponse;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.LayoutEmployeeMoneyBinding;
import hq.remview.databinding.LayoutEployeeDetailBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.setting.employee.EmployeeActivity;
import hq.remview.utils.AppUtils;
import hq.remview.utils.SimpleRecyclerViewAdapter;
import timber.log.Timber;

public class EmployeeDetailActivity extends BaseActivity<LayoutEployeeDetailBinding,EmployeeDetailViewModel> {
    private String currentCMD;
    public static final String EMPLOYEE_NAME = "EMPLOYEE_NAME";
    public static List<EmployeeResponse.Money> money;
    @Override
    public int getLayoutId() {
        return R.layout.layout_eployee_detail;
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
        getEmployeeName();
        setUpAdapter();
        setEmployeeTotalMoney();
        viewModel.tick.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Timber.d("tick");
                employeeMoney();
            }
        });
    }
    private void setEmployeeTotalMoney(){
        double sum = money.stream().mapToDouble(EmployeeResponse.Money::getMoney).sum()/100;

        viewBinding.setTotalMoney(AppUtils.formatDoubleMoneyHasCurrency(sum,
                ((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting()));

    }
    SimpleRecyclerViewAdapter<LayoutEmployeeMoneyBinding, SocketRevenueResponse> adapter;

    private void setUpAdapter(){
        adapter = new SimpleRecyclerViewAdapter<>(new SimpleRecyclerViewAdapter.SimpleRecyclerViewCallback<LayoutEmployeeMoneyBinding>() {
            @Override
            public LayoutEmployeeMoneyBinding setUpView(@NonNull ViewGroup parent, int viewType) {
                return LayoutEmployeeMoneyBinding.inflate(LayoutInflater.from(parent.getContext()));
            }

            @Override
            public void bindData(SimpleRecyclerViewAdapter.SimpleRecyclerViewAdapterViewHolder<LayoutEmployeeMoneyBinding> holder, int position) {
                holder.getView().setName(money.get(position).getName());
                double sum = Double.valueOf(money.get(position).getMoney())/100;

                String m = AppUtils.formatDoubleMoneyHasCurrency(sum,
                        ((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting());
                holder.getView().setMoney(m);
                holder.getView().executePendingBindings();
            }

            @Override
            public int size() {
                return money.size();
            }
        });

        DividerItemDecoration vertical = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        vertical.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.vertical_divider_small)));
        viewBinding.rv.addItemDecoration(vertical);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, RecyclerView.VERTICAL,false);

        DividerItemDecoration horizontal = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        horizontal.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.horizontal_divider_small)));
        viewBinding.rv.addItemDecoration(horizontal);
        viewBinding.rv.setLayoutManager(gridLayoutManager);
        viewBinding.rv.setAdapter(adapter);
    }
    private void getEmployeeName(){
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            String name = bundle.getString(EMPLOYEE_NAME,"");
            viewBinding.setName(name);
        }
    }
    @Override
    public void onMessage(SocketEventModel socketEventModel) {
        runOnUiThread(() -> {
            handleSocket(socketEventModel);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.disposeBag();
    }

    public void handleSocket(SocketEventModel socketEventModel){
        if(Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_MESSAGE)){
            if(socketEventModel.getMessage().getResponseCode() == 200){
                Timber.d("Screen %s",socketEventModel.getMessage().getScreen());
                if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals("EMPLOYEE_DETAIL_ACTIVITY")) {
                    Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                    return;
                }
                switch (socketEventModel.getMessage().getCmd()){
                    case Command.TINH_TIEN_NHAN_VIEN:
                    case Command.IN_DOANH_THU_NHAN_VIEN:
                        viewModel.hideLoading();
                        viewModel.showSuccessMessage("Thanh toán tiền nhân viên thành công");
                        
                        finish();
                        break;
                    case Command.LAY_DANH_SACH_NHAN_VIEN:
                        viewModel.startTimer();
                        Timber.d("Update employee money");
                        EmployeeResponse employeeResponse = ApiModelUtils.fromJson(socketEventModel.getMessage().getData().toString(),EmployeeResponse.class);
                        EmployeeResponse.EmployeObject employeObject = employeeResponse.getEmployees().stream().filter(o->o.employeeName.equals(viewBinding.getName())).findFirst().orElse(null);
                        if (employeObject == null){
                            finish();
                        } else {
                            money = employeObject.money;
                        }
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
            viewModel.hideLoading();
            viewModel.showErrorMessage(getString(R.string.can_not_connect_to_server));
        }
    }

    public void employeeMoney(){
        Message msg = new Message();
        msg.setScreen("EMPLOYEE_DETAIL_ACTIVITY");
        msg.setCmd(Command.LAY_DANH_SACH_NHAN_VIEN);
        currentCMD = Command.LAY_DANH_SACH_NHAN_VIEN;
        EmployeeMoneyRequest request = new EmployeeMoneyRequest();
        request.setFromPc(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting().getFromPcs());
        msg.setData(request);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    public void check(String name) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.employee_money_message)
                .setPositiveButton(R.string.yes,(dialog,id)->{
                    viewModel.showLoading();
                    Message msg = new Message();
                    msg.setScreen("EMPLOYEE_DETAIL_ACTIVITY");
                    msg.setCmd(Command.TINH_TIEN_NHAN_VIEN);
                    currentCMD = Command.TINH_TIEN_NHAN_VIEN;
                    EmployeeActivity.EmployeeRequest request = new EmployeeActivity.EmployeeRequest();
                    request.setPos((((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting().getFromPcs()));
                    request.setEmployee(name);
                    msg.setData(request);
                    msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
                    WebSocketLiveData.getInstance().sendEvent(msg);
                })
                .setNegativeButton(R.string.cancel,(dialog,id)->{
                    dialog.dismiss();
                })
                .create();
        alertDialog.show();
    }

    public void print(String name) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.employee_print_bill)
                .setPositiveButton(R.string.yes,(dialog,id)->{
                    viewModel.showLoading();
                    Message msg = new Message();
                    msg.setScreen("EMPLOYEE_DETAIL_ACTIVITY");
                    msg.setCmd(Command.IN_DOANH_THU_NHAN_VIEN);
                    currentCMD = Command.IN_DOANH_THU_NHAN_VIEN;
                    EmployeeActivity.EmployeeRequest request = new EmployeeActivity.EmployeeRequest();
                    request.setPos((((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting().getFromPcs()));
                    request.setEmployee(name);
                    msg.setData(request);
                    msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
                    WebSocketLiveData.getInstance().sendEvent(msg);
                })
                .setNegativeButton(R.string.cancel,(dialog, id)->{
                    dialog.dismiss();
                })
                .create();
        alertDialog.show();

    }
}
