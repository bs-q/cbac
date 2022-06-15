package hq.remview.ui.main.revenue.sell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Objects;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.data.model.api.response.SocketBillResponse;
import hq.remview.databinding.ActivitySellBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.revenue.sell.adapter.SellAdapter;
import hq.remview.ui.main.revenue.sell.detail.SellDetailActivity;
import hq.remview.utils.AppUtils;

public class SellActivity extends BaseActivity<ActivitySellBinding,SellViewModel>
        implements SellAdapter.SellItemClickListener, View.OnClickListener {
    public static final String BILLING_ITEM = "BILLING_ITEM";
    public static String jsonObject;
    @Override
    public int getLayoutId() {
        return R.layout.activity_sell;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }
    private SellAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding.mainAppBarQr.setVisibility(View.INVISIBLE);

        SocketBillResponse socketBillResponse = ApiModelUtils.fromJson(jsonObject, SocketBillResponse.class);
        viewModel.sellList = socketBillResponse.getDatas();

        // calculate total money
        double totalMoney = viewModel.sellList.stream().mapToDouble(value -> (double) (value.getTotalMoney() - value.getTotalMoney()*value.getPercent()/100) / 100).sum();
        String printTotalMoney = AppUtils.formatDoubleMoneyHasCurrency(totalMoney,((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting());
        viewBinding.setTotalMoney(printTotalMoney);

        adapter = new SellAdapter(this);
        adapter.setRestaurantSetting(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting());
        adapter.setContext(this);
        adapter.setItems(viewModel.sellList);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setOrientation(DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.vertical_divider_small)));
        viewBinding.recyclerView.addItemDecoration(divider);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        viewBinding.recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.restaurantName.set(((MVVMApplication)getApplication()).getCurrentRestaurant().getName());
    }

    @Override
    public void sellItemClick(BillingItemUnit item) {
        Intent it = new Intent(this, SellDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BILLING_ITEM, item);
        it.putExtras(bundle);
        startActivity(it);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        jsonObject = null;
        super.onDestroy();
    }
}