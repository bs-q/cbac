package hq.remview.ui.main.revenue.logs.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.databinding.BindingAdapters;
import hq.remview.R;
import hq.remview.data.model.api.obj.LogFoodItem;
import hq.remview.data.model.api.obj.RestaurantSetting;
import hq.remview.databinding.LayoutLogFoodItemBinding;
import hq.remview.ui.main.revenue.logs.LogFoodActivity;
import hq.remview.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;

public class LogFoodAdapter extends RecyclerView.Adapter<LogFoodAdapter.LogFoodAdapterViewHolder>{

    @Setter
    RestaurantSetting restaurantSetting;

    @Setter
    LogFoodActivity context;

    @Getter
    @Setter
    List<LogFoodItem> items = new ArrayList<>();

    private final LogFoodAdapter.LogFoodItemClickListener listener;
    public LogFoodAdapter(LogFoodAdapter.LogFoodItemClickListener listener){
        this.listener = listener;
    }



    @NonNull
    @Override
    public LogFoodAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutLogFoodItemBinding layoutLogFoodItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.layout_log_food_item,parent,false);
        return new LogFoodAdapterViewHolder(layoutLogFoodItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LogFoodAdapterViewHolder holder, int position) {
        holder.layoutLogFoodItemBinding.setItem(items.get(position));
        double pricePrint = (double)items.get(position).getTotalMoney()/100;
        holder.layoutLogFoodItemBinding.price.setText(AppUtils.formatDoubleMoneyHasCurrency(pricePrint,restaurantSetting));
        holder.layoutLogFoodItemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class LogFoodAdapterViewHolder extends RecyclerView.ViewHolder {
        LayoutLogFoodItemBinding layoutLogFoodItemBinding;

        public LogFoodAdapterViewHolder(@NonNull LayoutLogFoodItemBinding itemView) {
            super(itemView.getRoot());
            layoutLogFoodItemBinding = itemView;
        }
    }

    public interface LogFoodItemClickListener{
        void logItemClick(LogFoodItem item);
    }
}
