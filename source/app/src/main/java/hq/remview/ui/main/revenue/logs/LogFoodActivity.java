package hq.remview.ui.main.revenue.logs;

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
import hq.remview.data.model.api.obj.LogFoodItem;
import hq.remview.data.model.api.response.SocketLogFoodResponse;
import hq.remview.databinding.LayoutLogFoodBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.MainActivity;
import hq.remview.ui.main.revenue.logs.adapter.LogFoodAdapter;

public class LogFoodActivity extends BaseActivity<LayoutLogFoodBinding, LogFoodViewModel> implements LogFoodAdapter.LogFoodItemClickListener{

    private LogFoodAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding.mainAppBarQr.setVisibility(View.INVISIBLE);

        // extract data
        Bundle bundle = getIntent().getExtras();
        String jsonObject = bundle.getString(MainActivity.LOG_FOOD_ITEMS);
        SocketLogFoodResponse socketLogFoodResponse = ApiModelUtils.fromJson(jsonObject, SocketLogFoodResponse.class);
        viewModel.dataLogFood = socketLogFoodResponse.getDatas();

        adapter = new LogFoodAdapter(this);
        adapter.setRestaurantSetting(((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting());
        adapter.setContext(this);
        adapter.setItems(viewModel.dataLogFood);

        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.restaurantName.set(((MVVMApplication)getApplication()).getCurrentRestaurant().getName());
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_log_food;
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
    public void logItemClick(LogFoodItem item) {

    }
}
