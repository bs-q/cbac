package hq.remview.ui.main.revenue.revenue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import hq.remview.BR;
import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.ApiModelUtils;
import hq.remview.data.model.api.obj.DayRevenueUnit;
import hq.remview.data.model.api.request.SocketRevenueRequest;
import hq.remview.data.model.api.response.SocketRevenueResponse;
import hq.remview.data.websocket.Command;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.databinding.ActivityRevenueBinding;
import hq.remview.databinding.LayoutMoneyTypeBinding;
import hq.remview.di.component.ActivityComponent;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.MainActivity;
import hq.remview.utils.AppUtils;
import hq.remview.utils.SimpleRecyclerViewAdapter;
import timber.log.Timber;

public class RevenueActivity extends BaseActivity<ActivityRevenueBinding,RevenueViewModel>
implements View.OnClickListener {

    public static final String REVENUE_SCREEN ="REVENUE_SCREEN";


    @Override
    public int getLayoutId() {
        return R.layout.activity_revenue;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.header.mainAppBarQr.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        String jsonObject = bundle.getString(MainActivity.REVENUE_ITEMS);
        viewModel.listReportUnit = ApiModelUtils.fromJson(jsonObject,SocketRevenueResponse.class).getDataReport();
        viewModel.totalMoney.set(viewModel.listReportUnit.stream().mapToDouble(DayRevenueUnit::getTotalMoney).sum());

        viewBinding.setTotalMoney(AppUtils.formatDoubleMoneyHasCurrency(viewModel.totalMoney.get(),
                ((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting()));

        setUpAdapter();
        viewModel.tick.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                getRevenue();
            }
        });
        viewModel.startTimer();
    }

    @Override
    public void onMessage(SocketEventModel socketEventModel){
        runOnUiThread(() -> handleMessage(socketEventModel));
    }

    private void handleMessage(SocketEventModel socketEventModel){
        if(socketEventModel.getMessage().getResponseCode() == 200){
            if (socketEventModel.getMessage().getScreen()!=null&&!socketEventModel.getMessage().getScreen().equals(REVENUE_SCREEN)) {
                Timber.d("Block data from %s",socketEventModel.getMessage().getScreen());
                return;
            }
            switch (socketEventModel.getMessage().getCmd()){
                case Command.COMMAND_REPORT_REVENUE:
                    viewModel.hideLoading();
                    Timber.d("Update revenue revenue");
                    viewModel.startTimer();
                    updateAdapter(socketEventModel.getMessage().getData().toString());
                    break;
                default:
                    break;
            }
        }else{
            viewModel.hideLoading();
        }
    }

    private void updateAdapter(String jsonObject){
        viewModel.listReportUnit = ApiModelUtils.fromJson(jsonObject,SocketRevenueResponse.class).getDataReport();
        viewModel.totalMoney.set(viewModel.listReportUnit.stream().mapToDouble(DayRevenueUnit::getTotalMoney).sum());

        viewBinding.setTotalMoney(AppUtils.formatDoubleMoneyHasCurrency(viewModel.totalMoney.get(),
                ((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting()));
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onPause() {
        Timber.d("Remove Listen to socket");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Timber.d("Listen to socket");
        super.onResume();
        viewModel.restaurantName.set(((MVVMApplication)getApplication()).getCurrentRestaurant().getName());
    }

    SimpleRecyclerViewAdapter<LayoutMoneyTypeBinding, SocketRevenueResponse> adapter;

    private void setUpAdapter(){
        adapter = new SimpleRecyclerViewAdapter<>(new SimpleRecyclerViewAdapter.SimpleRecyclerViewCallback<LayoutMoneyTypeBinding>() {
            @Override
            public LayoutMoneyTypeBinding setUpView(@NonNull ViewGroup parent, int viewType) {
                return LayoutMoneyTypeBinding.inflate(LayoutInflater.from(parent.getContext()));
            }

            @Override
            public void bindData(SimpleRecyclerViewAdapter.SimpleRecyclerViewAdapterViewHolder<LayoutMoneyTypeBinding> holder, int position) {
                DayRevenueUnit itemObj = viewModel.listReportUnit.get(position);
                holder.getView().setItem(itemObj);
                holder.getView().setMoney(AppUtils.formatDoubleMoneyHasCurrency(itemObj.getTotalMoney(),
                        ((MVVMApplication)getApplication()).getCurrentRestaurant().getSetting()));
                holder.getView().executePendingBindings();
            }
            
            @Override
            public int size() {
                return viewModel.listReportUnit.size();
            }
        });

        DividerItemDecoration vertical = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        vertical.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.vertical_divider_small)));
        viewBinding.recyclerView.addItemDecoration(vertical);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);

        DividerItemDecoration horizontal = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        horizontal.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.horizontal_divider_small)));
        viewBinding.recyclerView.addItemDecoration(horizontal);
        viewBinding.recyclerView.setLayoutManager(gridLayoutManager);
        viewBinding.recyclerView.setAdapter(adapter);
    }
    /**
     * Day revenue
     * */
    public void getRevenue(){
        Timber.d("Get revenue");
        Message msg = new Message();
        msg.setScreen(REVENUE_SCREEN);
        msg.setCmd(Command.COMMAND_REPORT_REVENUE);
        msg.setToken(((MVVMApplication)getApplication()).getCurrentRestaurant().getToken());
        SocketRevenueRequest socketRevenueRequest = new SocketRevenueRequest();
        socketRevenueRequest.setKind(SocketRevenueRequest.REPORT_KIND_REVENUE_DAY);
        msg.setData(socketRevenueRequest);
        WebSocketLiveData.getInstance().sendEvent(msg);
    }

}
