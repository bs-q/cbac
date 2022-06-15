package hq.remview.ui.main.revenue.sell.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hq.remview.R;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.data.model.api.obj.RestaurantSetting;
import hq.remview.databinding.LayoutSellItemBinding;
import hq.remview.ui.main.revenue.sell.SellActivity;
import hq.remview.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.SellAdapterViewHolder> {
    @Setter
    RestaurantSetting restaurantSetting;

    @Setter
    SellActivity context;

    @Getter
    @Setter
    List<BillingItemUnit> items;

    public interface SellItemClickListener{
        void sellItemClick(BillingItemUnit item);
    }
    private final SellItemClickListener listener;
    public SellAdapter(SellItemClickListener listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public SellAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSellItemBinding layoutSellItemBinding = LayoutSellItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        layoutSellItemBinding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)context.getResources().getDimension(R.dimen._50sdp)));
        return new SellAdapterViewHolder(layoutSellItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SellAdapterViewHolder holder, int position) {
       holder.layoutSellItemBinding.setItem(items.get(position));
        double pricePrint = (items.get(position).getTotalMoney() - items.get(position).getTotalMoney()*items.get(position).getPercent()/100)/100;
        holder.layoutSellItemBinding.setMoney(AppUtils.formatDoubleMoneyHasCurrency(pricePrint,restaurantSetting));

       holder.layoutSellItemBinding.getRoot().setOnClickListener(v->listener.sellItemClick(
               holder.layoutSellItemBinding.getItem()
       ));
        holder.layoutSellItemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SellAdapterViewHolder extends RecyclerView.ViewHolder {
        LayoutSellItemBinding layoutSellItemBinding;

        public SellAdapterViewHolder(@NonNull LayoutSellItemBinding itemView) {
            super(itemView.getRoot());
            layoutSellItemBinding = itemView;
        }
    }
}

