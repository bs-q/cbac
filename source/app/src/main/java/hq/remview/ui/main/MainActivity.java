package hq.remview.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Date;
import java.util.Objects;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.request.EmployeeMoneyRequest;
import hq.remview.data.model.api.request.InReportRequest;
import hq.remview.data.model.api.request.SocketBillRequest;
import hq.remview.data.model.api.request.SocketLogFoodRequest;
import hq.remview.data.model.api.request.SocketReportRequest;
import hq.remview.data.model.api.request.SocketRevenueRequest;
import hq.remview.data.model.api.response.employee.EmployeeResponse;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import hq.remview.data.model.api.response.top.TopSaleResponse;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.ActivityMainBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.news.NewsFragment;
import hq.remview.ui.main.revenue.RevenueFragment;
import hq.remview.ui.main.revenue.RevenueViewModel;
import hq.remview.ui.main.revenue.logs.LogFoodActivity;
import hq.remview.ui.main.revenue.revenue.RevenueActivity;
import hq.remview.ui.main.revenue.sell.SellActivity;
import hq.remview.ui.main.revenue.statistic.StatisticActivity;
import hq.remview.ui.main.revenue.statistic.pie.PieChartActivity;
import hq.remview.ui.main.setting.SettingFragment;
import hq.remview.ui.main.setting.employee.EmployeeActivity;
import hq.remview.ui.main.setting.permission.PermissionActivity;
import hq.remview.ui.main.store.StoreFragment;
import hq.remview.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    public static final String MAIN_SCREEN ="MAIN_SCREEN";
    private Fragment active;
    private static final String STORE = "STORE";
    private static final String REVENUE = "REVENUE";
    private static final String NEWS = "NEWS";
    private static final String SETTING = "SETTING";

    public static final String REVENUE_ITEMS = "REVENUE_ITEMS";
    public static final String BILLING_ITEMS = "BILLING_ITEMS";
    public static final String LOG_FOOD_ITEMS = "LOG_FOOD_ITEMS";

    public int COMMAND_REPORT_REVENUE_TYPE = 13;
    private StoreFragment storeFragment;
    private RevenueFragment revenueFragment;
    private NewsFragment newsFragment;
    private SettingFragment settingFragment;
    private FragmentManager fm;
    @Setter
    @Getter
    private Date selectedDate = new Date();

    @Setter
    @Getter
    private Date statisticDateStart = new Date();
    @Setter
    @Getter
    private Date statisticDateEnd = new Date();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setMainActivity(this);
        viewBinding.setMainViewModel(viewModel);
        fm = getSupportFragmentManager();
        storeFragment = new StoreFragment();
        active = storeFragment;
        fm.beginTransaction().add(R.id.nav_host_fragment,storeFragment,STORE).commitNow();
        viewBinding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.store:
                    fm.beginTransaction().hide(active).show(storeFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
                    active = storeFragment;
                    return true;
                case R.id.revenue:
                    if (revenueFragment == null && viewModel.currentRestaurant != null && viewModel.currentRestaurant.isActive()){
                        revenueFragment = new RevenueFragment(viewModel.currentRestaurant);
                        fm.beginTransaction().add(R.id.nav_host_fragment, revenueFragment, REVENUE).hide(active).commit();
                    } else if(revenueFragment == null){
                        viewModel.showErrorMessage(getString(R.string.not_chose_restaurant));
                        return false;
                    } else {
                        fm.beginTransaction().hide(active).show(revenueFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
                    }
                    active = revenueFragment;
                    return true;
                case R.id.news:
                    if (newsFragment == null){
                        newsFragment = new NewsFragment();
                        fm.beginTransaction().add(R.id.nav_host_fragment, newsFragment, NEWS).hide(active).commit();
                    } else {
                        fm.beginTransaction().hide(active).show(newsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
                    }
                    active = newsFragment;
                    return true;
                case R.id.settings:
                    if (settingFragment == null && viewModel.currentRestaurant != null && viewModel.currentRestaurant.isActive()){
                        settingFragment = new SettingFragment();
                        fm.beginTransaction().add(R.id.nav_host_fragment, settingFragment, SETTING).hide(active).commit();
                    } else if(settingFragment == null){
                        viewModel.showErrorMessage(getString(R.string.not_chose_restaurant));
                        return false;
                    } else {
                        fm.beginTransaction().hide(active).show(settingFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
                    }
                    active = settingFragment;
                    return true;
                default:
                    break;
            }
            return false;
        });
        listenToSocketConnection();

    }
    @Override
    public void onMessage(SocketEventModel socketEventModel) {
        runOnUiThread(() -> {
            handleSocket(socketEventModel);
        });
    }

    private String currentCMD;
    public static int rds = 0;
    // handle
    public void handleSocket(SocketEventModel socketEventModel){
        if(Objects.equals(socketEventModel.getEvent(), SocketEventModel.EVENT_MESSAGE)){
            if(socketEventModel.getMessage().getResponseCode() == 200){
                Timber.d("Screen %s",socketEventModel.getMessage().getScreen());
                if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals(MAIN_SCREEN)) {
                    Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                    return;
                }
                switch (socketEventModel.getMessage().getCmd()){
                    case Command.COMMAND_VERIFY_QRCODE:
                        break;
                    case Command.COMMAND_TOKEN_CLIENT:
                        WebSocketLiveData.getInstance().setSession(viewModel.currentRestaurant.token);
                        ((MVVMApplication)getApplication()).setCurrentRestaurant(viewModel.currentRestaurant);
                        viewModel.currentRestaurant.lastAccessDate = new Date();
                        viewModel.addRestaurant(viewModel.currentRestaurant);
                        getSetting(viewModel.currentRestaurant);
                        break;
                    case Command.COMMAND_GET_SETTING:
                        viewModel.currentRestaurant.setActive(true);
                        navigateToRevenue(viewModel.currentRestaurant);
                        viewModel.currentRestaurant.setSetting(socketEventModel.getMessage().getData().toString());
                        viewModel.addRestaurant(viewModel.currentRestaurant);
                        viewModel.hideLoading();
                        break;
                    case Command.COMMAND_REPORT_REVENUE:
                        viewModel.hideLoading();
                        if (COMMAND_REPORT_REVENUE_TYPE == SocketRevenueRequest.REPORT_KIND_REVENUE_TOP_20) {
                            if (rds == 0) {
                                navigateToPieChart(socketEventModel.getMessage().getData().toString());
                            } else {
                                navigateToBarChart(socketEventModel.getMessage().getData().toString());
                            }
                        } else {
                            if (socketEventModel.getMessage().getData()!=null){
                                navigateToRevenueActivity(socketEventModel.getMessage().getData().toString());
                            } else {
                                viewModel.showErrorMessage("Null");
                            }
                        }
                        break;
                    case Command.COMMAND_GET_BILL:
                        viewModel.hideLoading();
                        if (socketEventModel.getMessage().getData()!=null){
                            navigateToBillActivity(socketEventModel.getMessage().getData().toString());
                        } else {
                            viewModel.showErrorMessage("Null");
                        }
                        break;
                    case  Command.COMMAND_GET_LOG_FOOD:
                        viewModel.hideLoading();
                        navigateToLogFoodActivity(socketEventModel.getMessage().getData().toString());
                        break;
                    case Command.IN_REPORT_Z:
                    case Command.BAT_TEAM_VIEW:
                    case Command.BAT_TUNNEL:
                    case Command.IN_REPORT_DAY:
                    case Command.IN_REPORT_MONTH:
                        viewModel.hideLoading();
                        break;
                    case Command.LAY_DANH_SACH_NHAN_VIEN:
                        navigateToEmployee(socketEventModel.getMessage().getData().toString());
                        viewModel.hideLoading();
                        break;
                    case Command.LAY_DANH_SACH_NHANVIEN_UPDATE:
                        navigateToPermission(socketEventModel.getMessage().getData().toString());
                        viewModel.hideLoading();
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
                        viewModel.currentRestaurant = null;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getBindingVariable() {
        return BR.mainViewModel;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    public void navigateToRevenue(RestaurantEntity entity){
//        if (revenueFragment == null){
//            viewModel.currentRestaurant = entity;
//            viewBinding.bottomNav.setSelectedItemId(R.id.revenue);
//        } else if (!Objects.equals(entity.id, viewModel.currentRestaurant.id)) {
//            viewModel.currentRestaurant = entity;
//            ((MVVMApplication)getApplication()).setCurrentRestaurant(viewModel.currentRestaurant);
//            ((RevenueViewModel) revenueFragment.getViewModel()).setCurrentRestaurant(viewModel.currentRestaurant);
//            ((RevenueViewModel) revenueFragment.getViewModel()).restaurantName.set(entity.name);
//            viewBinding.bottomNav.setSelectedItemId(R.id.revenue);
//        } else {
//            viewBinding.bottomNav.setSelectedItemId(R.id.revenue);
//        }

        if (revenueFragment == null){
            viewModel.currentRestaurant = entity;
            viewBinding.bottomNav.setSelectedItemId(R.id.revenue);
        } else {
            viewModel.currentRestaurant = entity;
            ((MVVMApplication)getApplication()).setCurrentRestaurant(viewModel.currentRestaurant);
            ((RevenueViewModel) revenueFragment.getViewModel()).setCurrentRestaurant(viewModel.currentRestaurant);
            ((RevenueViewModel) revenueFragment.getViewModel()).restaurantName.set(entity.name);
            viewBinding.bottomNav.setSelectedItemId(R.id.revenue);
        }

    }
    public void navigateToStore(){
        viewBinding.bottomNav.setSelectedItemId(R.id.store);
    }
    private void navigateToEmployee(String data){
        Intent it = new Intent(this, EmployeeActivity.class);
        EmployeeResponse employeeResponse = ApiModelUtils.fromJson(data,EmployeeResponse.class);
        EmployeeActivity.response = employeeResponse;
        startActivity(it);
    }

    @Override
    protected void onResume() {
        Timber.d("Listen to socket");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Timber.d("Remove Listen to socket");
        super.onPause();
    }

    public void verifyQRCode(String code){
        viewModel.showLoading();
        viewModel.verifyQrCode(code, new MainCalback() {
            @Override
            public void doError(Throwable error) {
                viewModel.hideLoading();
                viewModel.showErrorMessage(getString(R.string.connection_error));
                Timber.d(error);
            }

            @Override
            public void doSuccess() {
                storeFragment.addRestaurant(viewModel.token);
                storeFragment.showUpdateRestaurantNameDialog(viewModel.restaurantName, viewModel.restaurantId, viewModel.restaurantActive);
                viewModel.hideLoading();
            }

            @Override
            public void doFail() {
                viewModel.showErrorMessage(getString(R.string.verify_qr_code_error));
                viewModel.hideLoading();
            }
        });

    }
    public void verifyToken(RestaurantEntity entity){
        viewModel.currentRestaurant = entity;
        viewModel.currentRestaurant.setActive(false);

        Timber.d("verify token");
        viewModel.showLoading();
        viewModel.verifyToken(entity.token, new MainCalback() {
            @Override
            public void doError(Throwable error) {
                viewModel.hideLoading();
                viewModel.showErrorMessage(getString(R.string.connection_error));
                Timber.d(error);
            }

            @Override
            public void doFail() {
                viewModel.hideLoading();
                storeFragment.showCheckDialog();
            }

            @Override
            public void doSuccess() {
               verityTokenSocket(entity);
            }
        });
    }
    public void verityTokenSocket(RestaurantEntity entity){
        Timber.d("verify token socket");
        viewModel.currentRestaurant = entity;
        viewModel.showLoading();

        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.COMMAND_TOKEN_CLIENT);
        currentCMD = Command.COMMAND_TOKEN_CLIENT;
        msg.setToken(entity.token);

        WebSocketLiveData.getInstance().sendEvent(msg);

    }
    public void getSetting(RestaurantEntity entity){
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.COMMAND_GET_SETTING);
        currentCMD = Command.COMMAND_GET_SETTING;
        msg.setToken(entity.token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    /**
     * Day revenue
     * */
    public void getRevenue(RestaurantEntity entity){
        COMMAND_REPORT_REVENUE_TYPE = SocketRevenueRequest.REPORT_KIND_REVENUE_DAY;

        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.COMMAND_REPORT_REVENUE);
        currentCMD = Command.COMMAND_REPORT_REVENUE;
        msg.setToken(entity.token);
        SocketRevenueRequest socketRevenueRequest = new SocketRevenueRequest();
        socketRevenueRequest.setKind(SocketRevenueRequest.REPORT_KIND_REVENUE_DAY);
        msg.setData(socketRevenueRequest);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    /**
     * Get top sale
     * */
    public void getTop20(RestaurantEntity entity){
        COMMAND_REPORT_REVENUE_TYPE = SocketRevenueRequest.REPORT_KIND_REVENUE_TOP_20;
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.COMMAND_REPORT_REVENUE);
        currentCMD = Command.COMMAND_REPORT_REVENUE;
        msg.setToken(entity.token);
        SocketRevenueRequest socketRevenueRequest = new SocketRevenueRequest();
        socketRevenueRequest.setKind(SocketRevenueRequest.REPORT_KIND_REVENUE_TOP_20);
        socketRevenueRequest.setFromDate(AppUtils.formatDate(statisticDateStart, AppUtils.DATE_REQUEST_FORMAT));
        socketRevenueRequest.setToDate(AppUtils.formatDate(statisticDateEnd, AppUtils.DATE_REQUEST_FORMAT));
        socketRevenueRequest.setFromPc(viewModel.currentRestaurant.getSetting().getFromPcs());
        msg.setData(socketRevenueRequest);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    private void navigateToRevenueActivity(String jsonObject){
        Intent it = new Intent(this, RevenueActivity.class);
        it.putExtra(REVENUE_ITEMS,jsonObject);
        startActivity(it);
    }

    /**
     * List order of Day
     * - List bill of 1 day
     * */
    public void getListBillOfDay(RestaurantEntity entity){
        if(viewModel.currentRestaurant.getSetting()!=null){
            viewModel.showLoading();
            Message msg = new Message();
            msg.setScreen(MAIN_SCREEN);
            msg.setCmd(Command.COMMAND_GET_BILL);
            currentCMD = Command.COMMAND_GET_BILL;
            msg.setToken(entity.token);

            SocketBillRequest socketBillRequest = new SocketBillRequest();
            socketBillRequest.setFromDate(AppUtils.formatDate(statisticDateStart, AppUtils.DATE_REQUEST_FORMAT));
            socketBillRequest.setToDate(AppUtils.formatDate(statisticDateEnd, AppUtils.DATE_REQUEST_FORMAT));
            socketBillRequest.setFromPc(viewModel.currentRestaurant.getSetting().getFromPcs());
            msg.setData(socketBillRequest);
            WebSocketLiveData.getInstance().sendEvent(msg);
        }
    }
    private void navigateToBillActivity(String jsonObject){
        Intent it = new Intent(this, SellActivity.class);
        SellActivity.jsonObject = jsonObject;
        startActivity(it);
    }


    /**
     * Log food
     * */
    public void getLogFood(RestaurantEntity entity){
        if(viewModel.currentRestaurant.getSetting()!=null){
            viewModel.showLoading();
            Message msg = new Message();
            msg.setScreen(MAIN_SCREEN);
            msg.setCmd(Command.COMMAND_GET_LOG_FOOD);
            currentCMD = Command.COMMAND_GET_LOG_FOOD;
            msg.setToken(entity.token);
            SocketLogFoodRequest socketLogFoodRequest = new SocketLogFoodRequest();
            socketLogFoodRequest.setFromDate(AppUtils.formatDate(statisticDateStart, AppUtils.DATE_REQUEST_FORMAT));
            socketLogFoodRequest.setToDate(AppUtils.formatDate(statisticDateEnd, AppUtils.DATE_REQUEST_FORMAT));
            socketLogFoodRequest.setFromPc(viewModel.currentRestaurant.getSetting().getFromPcs());
            msg.setData(socketLogFoodRequest);
            WebSocketLiveData.getInstance().sendEvent(msg);
        }
    }
    public void inReportDay(String date,String endDate){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.IN_REPORT_DAY);
        currentCMD = Command.IN_REPORT_DAY;
        InReportRequest request = new InReportRequest();
        request.setFromDate(date);
        request.setToDate(endDate);
        request.setPos(((MVVMApplication) getApplication()).getCurrentRestaurant().getSetting().getFromPcs());
        msg.setData(request);
        msg.setToken(((MVVMApplication) getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    public void inReportMonth(String date){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.IN_REPORT_MONTH);
        currentCMD = Command.IN_REPORT_MONTH;
        InReportRequest request = new InReportRequest();
        request.setMonth(date);
        request.setPos(((MVVMApplication) getApplication()).getCurrentRestaurant().getSetting().getFromPcs());
        msg.setData(request);
        msg.setToken(((MVVMApplication) getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    public void openTeamview(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.BAT_TEAM_VIEW);
        currentCMD = Command.BAT_TEAM_VIEW;
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    public void openTunnel(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.BAT_TUNNEL);
        currentCMD = Command.BAT_TUNNEL;
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    public void employeePermission(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.LAY_DANH_SACH_NHANVIEN_UPDATE);
        currentCMD = Command.LAY_DANH_SACH_NHANVIEN_UPDATE;
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }
    public void employeeMoney(){
        viewModel.showLoading();
        Message msg = new Message();
        msg.setScreen(MAIN_SCREEN);
        msg.setCmd(Command.LAY_DANH_SACH_NHAN_VIEN);
        currentCMD = Command.LAY_DANH_SACH_NHAN_VIEN;
        EmployeeMoneyRequest request = new EmployeeMoneyRequest();
        request.setFromPc(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting().getFromPcs());
        msg.setData(request);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().token);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

    private void navigateToBarChart(String data){
        Intent it = new Intent(this, StatisticActivity.class);
        TopSaleResponse topSaleResponse = ApiModelUtils.fromJson(data,TopSaleResponse.class);
        StatisticActivity.topSaleItems = topSaleResponse.getDatas();
        startActivity(it);
    }

    private void navigateToPieChart(String data){
        Intent it = new Intent(this, PieChartActivity.class);
        TopSaleResponse topSaleResponse = ApiModelUtils.fromJson(data,TopSaleResponse.class);
        PieChartActivity.topSaleItems = topSaleResponse.getDatas();
        startActivity(it);
    }

    private void navigateToLogFoodActivity(String jsonObject){
        Intent it = new Intent(this, LogFoodActivity.class);
        it.putExtra(LOG_FOOD_ITEMS,jsonObject);
        startActivity(it);
    }
    private void navigateToPermission(String json){
        Intent it = new Intent(this, PermissionActivity.class);
        PermissionActivity.permissionResponse = ApiModelUtils.fromJson(json,PermissionResponse.class);
        startActivity(it);
    }

    AlertDialog alertDialog;
    @Override
    public void onBackPressed() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.quit_application)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    MainActivity.super.onBackPressed();
                    System.exit(0);
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }
    public void recreateScreen(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
