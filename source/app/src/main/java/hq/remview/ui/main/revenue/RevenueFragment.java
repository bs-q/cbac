package hq.remview.ui.main.revenue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.util.Pair;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;
import hq.remview.BR;
import hq.remview.R;
import hq.remview.data.model.api.request.VerifyQRCodeRequest;
import hq.remview.data.model.api.request.WebQRCodeRequest;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.databinding.FragmentRevenueBinding;
import hq.remview.di.component.FragmentComponent;
import hq.remview.ui.base.fragment.BaseFragment;
import hq.remview.ui.base.fragment.BaseFragmentViewModel;
import hq.remview.ui.main.MainActivity;
import hq.remview.ui.main.revenue.adapter.HeaderItem;
import hq.remview.ui.main.revenue.adapter.RevenueItem;
import hq.remview.ui.main.revenue.adapter.RevenueItemClick;
import hq.remview.ui.main.revenue.sell.SellActivity;
import hq.remview.ui.main.revenue.statistic.StatisticActivity;
import hq.remview.ui.main.revenue.statistic.pie.PieChartActivity;
import hq.remview.ui.main.scanner.CustomScanner;
import timber.log.Timber;

public class RevenueFragment extends BaseFragment<FragmentRevenueBinding,RevenueViewModel>
        implements View.OnClickListener, RevenueItemClick {
    private static final int REQUEST_OPEN_SCAN_QRCODE = 2309;
    private static final int REQUEST_OPEN_SCAN_QRCODE_FROM_PC = 2139;

    private int scanType;
    private RestaurantEntity initRestaurant;
    private MaterialDatePicker<Long> calendar;
    private MaterialDatePicker<Pair<Long, Long>> statisticCalendar;

    public RevenueFragment(){

    }

    public RevenueFragment(RestaurantEntity entity) {
        initRestaurant = entity;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_revenue;
    }

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        viewModel.currentRestaurant = initRestaurant;
        // setting details page
        viewModel.detailSelected.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (((ObservableBoolean) sender).get()) {
//                    binding.header.revenueBack.setVisibility(View.VISIBLE);
                    binding.listItem.getRoot().setVisibility(View.INVISIBLE);
//                    binding.header.mainAppBarQr.setVisibility(View.INVISIBLE);
                } else {
//                    binding.header.revenueBack.setVisibility(View.INVISIBLE);
                    binding.listItem.getRoot().setVisibility(View.VISIBLE);
//                    binding.header.mainAppBarQr.setVisibility(View.VISIBLE);

                }
            }
        });
        adapter = new FlexibleAdapter<>(setupAdapterData());
        setupAdapter();
    }
    FlexibleAdapter<IFlexible> adapter;
    HeaderItem sellList;
    HeaderItem statisticList;
    private void setupAdapter(){
        adapter.setStickyHeaders(true).setDisplayHeadersAtStartUp(true);
        GridLayoutManager gridLayoutManager = new SmoothScrollGridLayoutManager(requireActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItem(position).getSpanSize(2, position);
            }
        });
        binding.listItem.rv.addItemDecoration(new FlexibleItemDecoration(requireActivity())
                .addItemViewType(R.layout.layout_revenue_grid_item,4,0,4,4)
        );
        binding.listItem.rv.setLayoutManager(gridLayoutManager);
        binding.listItem.rv.setHasFixedSize(true);
        binding.listItem.rv.setAdapter(adapter);
    }

    private List<IFlexible> setupAdapterData(){
        List<IFlexible> list = new ArrayList<>();
        sellList = new HeaderItem(1,R.string.sell_list,this);
        statisticList = new HeaderItem(2,R.string.analytic,this);
        // list
        Integer[] sellIcon = new Integer[]{R.drawable.sale_icon,R.drawable.cancel_icon};
        Integer[] sellText = new Integer[]{R.string.revenuesale,R.string.revenuecancel};

        for (int i = 0; i < sellIcon.length; i++){
            RevenueItem item = new RevenueItem(sellIcon[i],sellList,sellText[i],this);
            list.add(item);
        }

        // statistic
        Integer[] statisticIcon = new Integer[]{
                R.drawable.top_sale
                ,R.drawable.sort_type
                , R.drawable.sort_revenue
                , R.drawable.sort_month
                , R.drawable.sort_member
                , R.drawable.sort_house
                , R.drawable.sort_machine
                , R.drawable.sort_product
                , R.drawable.sort_week
                , R.drawable.sort_time
        };
        Integer[] statisticText = new Integer[]{
        R.string.top20
        ,R.string.typeincome
        ,R.string.monthincome
        ,R.string.incomedayofweek
        ,R.string.memberincome
        ,R.string.houseincome
        ,R.string.machineincome
        ,R.string.quantityincome
        ,R.string.weekincome
        ,R.string.incomehour};

        for (int i = 0; i < statisticIcon.length; i++){
            RevenueItem item = new RevenueItem(statisticIcon[i],statisticList,statisticText[i],this);
            list.add(item);
        }
        return list;
    }


    @Override
    public void onStart() {
        super.onStart();

    }



    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
    public void openScanQRCode() {
        ScanOptions integrator = new ScanOptions();
        integrator.setCaptureActivity(CustomScanner.class);
        integrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setPrompt("");
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        scan.launch(integrator);
    }
    private final ActivityResultLauncher<ScanOptions> scan = registerForActivityResult(new ScanContract(), result -> {
        if (scanType == REQUEST_OPEN_SCAN_QRCODE) {
            Timber.d("Handle request from mobile");
            handleQRCode(result.getContents());
        } else if (scanType == REQUEST_OPEN_SCAN_QRCODE_FROM_PC) {
            Timber.d("Handle request from pc");
            handleQRCodeFromPC(result.getContents());
        }

    });


    private void handleQRCodeFromPC(String result){
        viewModel.showSuccessMessage(getString(R.string.scan_qr_success));
        sendQRCodeToWebView(result);
    }
    private void sendQRCodeToWebView(String qrCode ){
        Timber.d("Send to qrcode pc: %s", qrCode);
        WebQRCodeRequest webQRCodeRequest = new WebQRCodeRequest();
        webQRCodeRequest.setQrCode(qrCode);

    }
    private void handleQRCode(String result){
        if (result == null) {
            viewModel.showErrorMessage(getString(R.string.scan_qr_code_cancelled));
        } else {

            try {
                String id = result.substring(result.indexOf("::") + 2, result.lastIndexOf("::"));
                String name = result.substring(result.lastIndexOf("::") + 2);
                String doSend = result.substring(0, result.lastIndexOf("::"));
                viewModel.currentRestaurant = new RestaurantEntity();
                viewModel.currentRestaurant.setLastAccessDate(new Date());
                viewModel.currentRestaurant.setId(id);
                viewModel.currentRestaurant.setName(name);
                doVerifyQRCode(doSend);
            } catch (IndexOutOfBoundsException e) {
                viewModel.showErrorMessage(getString(R.string.qr_code_wrong_format));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel.currentRestaurant == null){
//            viewModel.showErrorMessage(getString(R.string.not_chose_restaurant));
            ((MainActivity) requireActivity()).navigateToStore();
        }else if ( viewModel.restaurantName.get().equals("")){
            viewModel.restaurantName.set(initRestaurant.name);
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_app_bar_qr:
            case R.id.scan_qr_code:
                scanQrCode();
                break;
            case R.id.revenue_item:
                ((MainActivity) requireActivity()).getRevenue(viewModel.currentRestaurant);
                break;
            case R.id.main_app_bar_calendar:
                openCalendar();
                break;
            default:
                break;
        }
    }
    private void navigateToSellActivity(){
        Intent it = new Intent(requireActivity(), SellActivity.class);
        startActivity(it);
    }



    private void scanQrCode() {
        scanType = REQUEST_OPEN_SCAN_QRCODE_FROM_PC;
        openScanQRCode();
    }

    private void doVerifyQRCode(String message) {
        Timber.d("Send to nodejs%s", message);
        VerifyQRCodeRequest verifyQRCodeRequest = new VerifyQRCodeRequest();
        verifyQRCodeRequest.setDeviceId(deviceId);
        verifyQRCodeRequest.setQrCode(message);
    }
    public BaseFragmentViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void itemClick(Integer id) {
        switch (id) {
            case R.drawable.sale_icon:
                ((MainActivity) requireActivity()).getListBillOfDay(viewModel.currentRestaurant);
                break;
            case R.drawable.cancel_icon:
                ((MainActivity) requireActivity()).getLogFood(viewModel.currentRestaurant);
                break;
            case R.drawable.sort_type:
//                navigateToBarChart();
                break;
            case R.drawable.sort_revenue:
//                navigateToPieChart();
                break;
            case R.drawable.top_sale:
                MainActivity.rds = 1;
                ((MainActivity) requireActivity()).getTop20(viewModel.currentRestaurant);
                break;
            case R.drawable.sort_house:
                MainActivity.rds = 0;
                ((MainActivity) requireActivity()).getTop20(viewModel.currentRestaurant);
                break;
            default:
                break;
        }
    }

    private void navigateToBarChart(){
        Intent it = new Intent(requireActivity(), StatisticActivity.class);
        startActivity(it);
    }
    private void navigateToPieChart(){
        Intent it = new Intent(requireActivity(), PieChartActivity.class);
        startActivity(it);
    }

    private void openCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH,-90);
        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();
        CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(cal.getTimeInMillis());
        CalendarConstraints.DateValidator dateValidatorMax = DateValidatorPointBackward.before((new Date()).getTime());
        ArrayList<CalendarConstraints.DateValidator> listValidators =
                new ArrayList<>();
        listValidators.add(dateValidatorMin);
        listValidators.add(dateValidatorMax);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        calendarConstraints.setValidator(validators);
        statisticCalendar = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.choose_day)
                .setCalendarConstraints(calendarConstraints.build())
                .setTheme(R.style.MaterialCalendarTheme)
                .build();
        statisticCalendar.addOnPositiveButtonClickListener(selection -> {
            String startDate = String.format(this.getString(R.string.date_format),new Date(selection.first));
            String endDate = String.format(this.getString(R.string.date_format),new Date(selection.second));
            if (startDate.equals(endDate)){
                binding.listItem.date.setText(startDate);
            } else {
                binding.listItem.date.setText(startDate + " - " + endDate);
            }
            ((MainActivity)requireActivity()).setStatisticDateStart(new Date(selection.first));
            ((MainActivity)requireActivity()).setStatisticDateEnd(new Date(selection.second));
        });
        statisticCalendar.show(getParentFragmentManager(), "calendar");
    }

}
