package hq.remview.ui.main.setting;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollGridLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;
import hq.remview.R;
import hq.remview.databinding.FragmentSettingBinding;
import hq.remview.di.component.FragmentComponent;
import hq.remview.ui.base.fragment.BaseFragment;
import hq.remview.ui.main.MainActivity;
import hq.remview.ui.main.setting.adapter.HeaderItem;
import hq.remview.ui.main.setting.adapter.SettingItem;
import hq.remview.ui.main.setting.adapter.SettingItemClick;
import hq.remview.ui.main.setting.permission.PermissionActivity;
import hq.remview.utils.AppUtils;
import timber.log.Timber;

public class SettingFragment extends BaseFragment<FragmentSettingBinding,SettingViewModel> implements
        View.OnClickListener, SettingItemClick {
    private MaterialDatePicker<Pair<Long,Long>> statisticCalendar;

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void performDataBinding() {
//        adapter = new SettingAdapter();
//        adapter.setName(new Integer[]{R.string.print_bill, R.string.employee_money,R.string.open_teamview,R.string.open_tunnel});
//        adapter.setCallback(this);
//        int spacing = getResources().getDimensionPixelSize(R.dimen._5sdp);
//        binding.rv.addItemDecoration(new GridSpacingItemDecoration(2,spacing,2*spacing,true));
//        binding.rv.setLayoutManager(new GridLayoutManager(requireContext(),2));
//        binding.rv.setAdapter(adapter);
        binding.setF(this);
        binding.setVm(viewModel);
        adapter = new FlexibleAdapter<>(setupAdapterData());
        setupAdapter();
    }
    HeaderItem employeePayment;
    HeaderItem summary;
    HeaderItem permission;
    HeaderItem edit;
    private List<IFlexible> setupAdapterData(){
        List<IFlexible> list = new ArrayList<>();
        employeePayment = new HeaderItem(1,R.string.employeepayment,this);
        summary = new HeaderItem(2,R.string.summaryofdaysandmonths,this);
        permission = new HeaderItem(1,R.string.permission,this);
        edit = new HeaderItem(2,R.string.edititem,this);
        // list
        Integer[] employeePaymentIc = new Integer[]{R.drawable.employee_revenue};
        Integer[] employeePaymentTxt = new Integer[]{R.string.employee_money};

        Integer[] summaryIc = new Integer[]{R.drawable.print_report_day};
        Integer[] summaryTxt = new Integer[]{R.string.print_bill};

        Integer[] summaryMonthIc = new Integer[]{R.drawable.print_report_month};
        Integer[] summaryMonthTxt = new Integer[]{R.string.print_bill_month};

        Integer[] permissionIc = new Integer[]{R.drawable.manage_employee};
        Integer[] permissionTxt = new Integer[]{R.string.editpermission};

        Integer[] editIc = new Integer[]{R.drawable.manage_product};
        Integer[] editTxt = new Integer[]{R.string.edititem};

        list.add(new SettingItem(employeePaymentIc[0],employeePayment,employeePaymentTxt[0],this));
        list.add(new SettingItem(summaryIc[0],summary,summaryTxt[0],this));
        list.add(new SettingItem(summaryMonthIc[0],summary,summaryMonthTxt[0],this));
        list.add(new SettingItem(permissionIc[0],permission,permissionTxt[0],this));
        list.add(new SettingItem(editIc[0],edit,editTxt[0],this));

        return list;
    }
    FlexibleAdapter<IFlexible> adapter;

    private void setupAdapter(){
        adapter.setStickyHeaders(false).setDisplayHeadersAtStartUp(true);
        GridLayoutManager gridLayoutManager = new SmoothScrollGridLayoutManager(requireActivity(), 1);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItem(position).getSpanSize(1, position);
            }
        });
        binding.rv.addItemDecoration(new FlexibleItemDecoration(requireActivity())
                .addItemViewType(R.layout.layout_revenue_grid_item,4,0,4,4)
        );
        binding.rv.setLayoutManager(gridLayoutManager);
        binding.rv.setHasFixedSize(true);
        binding.rv.setAdapter(adapter);
    }
    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void itemClick(Integer value) {
        if (value == R.string.employee_money){
            ((MainActivity)requireActivity()).employeeMoney();
        } else if (value == R.string.print_bill){
            openCalendar();
        } else if (value == R.string.editpermission){
            ((MainActivity)requireActivity()).employeePermission();
        } else if (value == R.string.print_bill_month){
            openMonthCalendar();
        }
    }
    private void openMonthCalendar(){
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(requireActivity(), new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                Date date = new GregorianCalendar(selectedYear, selectedMonth , 1).getTime();
                String startDate = AppUtils.formatDate(date, AppUtils.DATE_REQUEST_FORMAT);
                ((MainActivity)requireActivity()).inReportMonth(startDate);

            }
        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder
//                .setActivatedMonth(Calendar.JULY)
//                .setMinYear(1990)
//                .setActivatedYear(2017)
//                .setMaxYear(2030)
//                .setMinMonth(Calendar.FEBRUARY)
//                .setTitle("Select trading month")
//                .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                // .setMaxMonth(Calendar.OCTOBER)
                // .setYearRange(1890, 1890)
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
//                .showMonthOnly()
                // .showYearOnly()
                .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                    @Override
                    public void onMonthChanged(int selectedMonth) {

                    }
                })
                .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                    @Override
                    public void onYearChanged(int selectedYear) {

                    }
                })
                .build()
                .show();


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

            String startDate = AppUtils.formatDate(new Date(selection.first), AppUtils.DATE_REQUEST_FORMAT);
            String endDate = AppUtils.formatDate(new Date(selection.second), AppUtils.DATE_REQUEST_FORMAT);
            ((MainActivity)requireActivity()).inReportDay(startDate,endDate);
        });
        statisticCalendar.show(getParentFragmentManager(), "calendar");
    }

}
