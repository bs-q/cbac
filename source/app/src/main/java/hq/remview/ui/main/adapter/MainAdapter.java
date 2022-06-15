package hq.remview.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.CornerFamily;

import java.util.List;

import hq.remview.R;
import hq.remview.databinding.RestaurantItemLayoutBinding;
import hq.remview.packages.swipe.SwipeRevealLayout;
import hq.remview.packages.swipe.ViewBinderHelper;
import hq.remview.ui.base.adapter.OnItemClickListener;
import lombok.Setter;
import timber.log.Timber;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private List<MainItem> restaurantEntities;
    public Integer current;
    @Setter
    private OnItemClickListener clickListener;
    public MainAdapter(List<MainItem> restaurantEntities) {
        this.restaurantEntities = restaurantEntities;
        binderHelper.setOpenOnlyOne(true);
    }
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private float radius;
    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        radius = parent.getContext().getResources().getDimension(R.dimen._7sdp);

        RestaurantItemLayoutBinding restaurantItemLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.restaurant_item_layout,parent,false);
        return new MainViewHolder(restaurantItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        MainItem restaurantEntity =restaurantEntities.get(position);
        holder.restaurantItemLayoutBinding.setItem(restaurantEntity);
        binderHelper.bind(holder.restaurantItemLayoutBinding.swipeLayout, restaurantEntity.getEntity().id);
        holder.restaurantItemLayoutBinding.delete.setShapeAppearanceModel(
                holder.restaurantItemLayoutBinding.delete.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED,0)
                        .setTopRightCorner(CornerFamily.ROUNDED,radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                        .setBottomLeftCornerSize(0)
                        .build());
        holder.restaurantItemLayoutBinding.swipeLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
                holder.restaurantItemLayoutBinding.card.setShapeAppearanceModel(
                        holder.restaurantItemLayoutBinding.card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                .setTopRightCorner(CornerFamily.ROUNDED,radius)
                                .setBottomRightCorner(CornerFamily.ROUNDED,radius)
                                .setBottomLeftCornerSize(radius)
                                .build());
                restaurantEntity.setShowMenu(false);
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                holder.restaurantItemLayoutBinding.card.setShapeAppearanceModel(
                        holder.restaurantItemLayoutBinding.card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                .setTopRightCorner(CornerFamily.ROUNDED,0)
                                .setBottomRightCorner(CornerFamily.ROUNDED,0)
                                .setBottomLeftCornerSize(radius)
                        .build());
                current = position;
                restaurantEntity.setShowMenu(false);
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
                if (!restaurantEntity.isShowMenu()){
                    holder.restaurantItemLayoutBinding.card.setShapeAppearanceModel(
                            holder.restaurantItemLayoutBinding.card.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                    .setTopRightCorner(CornerFamily.ROUNDED,0)
                                    .setBottomRightCorner(CornerFamily.ROUNDED,0)
                                    .setBottomLeftCornerSize(radius)
                                    .build());
                    restaurantEntity.setShowMenu(true);
                }
            }
        });

        holder.restaurantItemLayoutBinding.delete.setOnClickListener(v -> {
            Timber.d("delete click");
            clickListener.onItemDelete(position);
            holder.restaurantItemLayoutBinding.swipeLayout.close(true);
        });
        holder.restaurantItemLayoutBinding.content.setOnClickListener(v -> {
            Timber.d("card click");
            closeCurrent();
            current = position;
            if (clickListener!=null){
                clickListener.onItemClick(position);
            }
        });

        holder.restaurantItemLayoutBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return restaurantEntities.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        private final RestaurantItemLayoutBinding restaurantItemLayoutBinding;

        public MainViewHolder(@NonNull RestaurantItemLayoutBinding restaurantItemLayoutBinding) {
            super(restaurantItemLayoutBinding.getRoot());
            this.restaurantItemLayoutBinding = restaurantItemLayoutBinding;
        }
    }
    public void closeCurrent(){
        if (current == null || restaurantEntities.isEmpty())
            return;
        binderHelper.closeLayout(restaurantEntities.get(current).getEntity().id);
    }

}
