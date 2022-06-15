package hq.remview.ui.main.revenue.sell.detail.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hq.remview.R;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.data.model.api.obj.FoodItem;
import hq.remview.data.model.api.obj.RestaurantSetting;
import hq.remview.databinding.LayoutSellDetailFooterBinding;
import hq.remview.databinding.LayoutSellDetailHeaderBinding;
import hq.remview.databinding.LayoutSellDetailItemBinding;
import hq.remview.ui.main.revenue.sell.detail.SellDetailActivity;
import hq.remview.utils.AppUtils;
import lombok.Setter;

public class SellDetailAdapter extends RecyclerView.Adapter<SellDetailAdapter.SellDetailAdapterViewHolder> {
    public static final String DELIM_1 = "¶\\*¶";
    public static final String DELIM_2 = "█";
    public static final String DELIM_3 = "Ө";
    public static final String DELIM_4 = "≡";
    public static final String DELIM_5 = "≠";
    public static final String DELIM_6 = "------------------------------";

    private BillingItemUnit detail;

    @Setter
    SellDetailActivity context;

    @Setter
    RestaurantSetting restaurantSetting;

    private List<FoodItem> foodItems = new ArrayList<>();

    public void setDetail(BillingItemUnit detail) {
        this.detail = detail;
        //parser food
        String[] allRecords = detail.getMonAn().split(DELIM_1);
        if(allRecords!=null){
            FoodItem foodItem;
            for(int i =0; i< allRecords.length; i++){
                if(!allRecords[i].isEmpty() && !allRecords[i].equals(DELIM_6)){
                    foodItem = new FoodItem();
                    String[] recordDetail = allRecords[i].split(DELIM_2);

                    String[] foodDetail = recordDetail[1].split(DELIM_3);
                    foodItem.setAmount(AppUtils.parseInt(foodDetail[0]));
                    foodItem.setName(foodDetail[1]);
                    foodItem.setPrice(AppUtils.converDouble(restaurantSetting,foodDetail[2]));
                    foodItem.setFoodType(FoodItem.FOOD_TYPE_FOOD);
                    foodItems.add(foodItem);

                    //Check beilage
                    if(foodDetail.length > 5){
                        parserBeilager(foodDetail[5]);
                    }

                }
            }
        }

    }

    private void parserBeilager(String b){
        String[] beilageFoods = b.split(DELIM_4);
        if(beilageFoods!=null && beilageFoods.length > 0){
            FoodItem beilage;
            for(int i =0; i< beilageFoods.length; i++){
                beilage = new FoodItem();
                beilage.setFoodType(FoodItem.FOOD_TYPE_BEILAGE);
                if(!beilageFoods[i].isEmpty()){
                    String[] beilageDetail = beilageFoods[i].split(DELIM_5);
                    beilage.setName(beilageDetail[0]);
                    beilage.setPrice(AppUtils.converDouble(restaurantSetting,beilageDetail[2]));
                    foodItems.add(beilage);
                }
            }
        }
    }

    @NonNull
    @Override
    public SellDetailAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                LayoutSellDetailHeaderBinding layoutSellDetailHeaderBinding = LayoutSellDetailHeaderBinding.inflate(LayoutInflater.from(parent.getContext()));
                return new SellDetailAdapterViewHolder(layoutSellDetailHeaderBinding);
            case 1:
                LayoutSellDetailItemBinding layoutSellDetailItemBinding = LayoutSellDetailItemBinding.inflate(LayoutInflater.from(parent.getContext()));
                layoutSellDetailItemBinding.getRoot().setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)context.getResources().getDimension(R.dimen._30sdp)));
                return new SellDetailAdapterViewHolder(layoutSellDetailItemBinding);
            case 2:
                LayoutSellDetailFooterBinding layoutSellDetailFooterBinding = LayoutSellDetailFooterBinding.inflate(LayoutInflater.from(parent.getContext()));
                return new SellDetailAdapterViewHolder(layoutSellDetailFooterBinding);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SellDetailAdapterViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                holder.layoutSellDetailHeaderBinding.setItem(detail);
                holder.layoutSellDetailHeaderBinding.executePendingBindings();
                break;
            case 1:
                holder.layoutSellDetailItemBinding.setName(foodItems.get(position-1).getName());
                holder.layoutSellDetailItemBinding.setType(foodItems.get(position-1).getFoodType());
                if(foodItems.get(position-1).getFoodType() == FoodItem.FOOD_TYPE_FOOD){
                    holder.layoutSellDetailItemBinding.setQuantity("x"+foodItems.get(position-1).getAmount());
                }else{
                    holder.layoutSellDetailItemBinding.setQuantity("");
                }
                if(foodItems.get(position-1).getPrice() != 0){
                    holder.layoutSellDetailItemBinding.setMoney(AppUtils.formatDoubleMoneyHasCurrency(foodItems.get(position-1).getPrice(),restaurantSetting));
                }else{
                    holder.layoutSellDetailItemBinding.setMoney("");
                }


                if (position == foodItems.size()){
                    holder.layoutSellDetailItemBinding.setLine(false);
                } else {
                    holder.layoutSellDetailItemBinding.setLine(true);
                }
                holder.layoutSellDetailItemBinding.executePendingBindings();
                break;
            case 2:
                double pricePrint = (double)detail.getTotalMoney()/100;
                holder.layoutSellDetailFooterBinding.setMoney(AppUtils.formatDoubleMoneyHasCurrency(pricePrint,restaurantSetting));
                holder.layoutSellDetailFooterBinding.executePendingBindings();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return foodItems.size()+2;
        //return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return  0;
        } else if (position == foodItems.size()+1){
            return 2;
        }
        return  1;
    }

    public static class SellDetailAdapterViewHolder extends RecyclerView.ViewHolder {
        LayoutSellDetailHeaderBinding layoutSellDetailHeaderBinding;
        LayoutSellDetailFooterBinding layoutSellDetailFooterBinding;
        LayoutSellDetailItemBinding layoutSellDetailItemBinding;
        public SellDetailAdapterViewHolder(@NonNull LayoutSellDetailHeaderBinding layoutSellDetailHeaderBinding) {
            super(layoutSellDetailHeaderBinding.getRoot());
            this.layoutSellDetailHeaderBinding = layoutSellDetailHeaderBinding;

        }
        public SellDetailAdapterViewHolder(@NonNull LayoutSellDetailFooterBinding layoutSellDetailFooterBinding) {
            super(layoutSellDetailFooterBinding.getRoot());
            this.layoutSellDetailFooterBinding = layoutSellDetailFooterBinding;
        }
        public SellDetailAdapterViewHolder(@NonNull LayoutSellDetailItemBinding layoutSellDetailItemBinding) {
            super(layoutSellDetailItemBinding.getRoot());
            this.layoutSellDetailItemBinding = layoutSellDetailItemBinding;
        }
    }
}
