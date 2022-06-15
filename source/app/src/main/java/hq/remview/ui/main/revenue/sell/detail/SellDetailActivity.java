package hq.remview.ui.main.revenue.sell.detail;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.databinding.ActivitySellDetailBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.revenue.sell.SellActivity;
import hq.remview.ui.main.revenue.sell.detail.adapter.SellDetailAdapter;

public class SellDetailActivity extends BaseActivity<ActivitySellDetailBinding,SellDetailViewModel>
implements View.OnClickListener {

    @Override
    public int getLayoutId() {
        return R.layout.activity_sell_detail;
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
    public void onClick(View v) {

    }
    private SellDetailAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        viewBinding.header.mainAppBarQr.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        BillingItemUnit billingItemUnit = (BillingItemUnit)bundle.getSerializable(SellActivity.BILLING_ITEM);
        viewModel.billingItemUnit = billingItemUnit;

        adapter = new SellDetailAdapter();
        adapter.setRestaurantSetting(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting());
        adapter.setDetail(billingItemUnit);
        adapter.setContext(this);

        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.restaurantName.set(((MVVMApplication)getApplication()).getCurrentRestaurant().getName());
    }
}