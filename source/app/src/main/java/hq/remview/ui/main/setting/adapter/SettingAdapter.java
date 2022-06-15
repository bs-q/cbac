package hq.remview.ui.main.setting.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import hq.remview.R;
import hq.remview.databinding.LayoutSettingItemBinding;
import lombok.Setter;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder> {
    public interface SettingAdapterCallback{
        void itemClick(Integer value);
    }
    @Setter
    SettingAdapterCallback callback;

    @Setter
    Integer [] name;
    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSettingItemBinding l = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_setting_item,parent,false);
        return new SettingViewHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        holder.l.setName(holder.l.getRoot().getContext().getString(name[position]));
        holder.l.getRoot().setOnClickListener(v->callback.itemClick(name[position]));
        holder.l.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return name.length;
    }

    public static class SettingViewHolder extends RecyclerView.ViewHolder {
        LayoutSettingItemBinding l;
        public SettingViewHolder(@NonNull LayoutSettingItemBinding l) {
            super(l.getRoot());
            this.l = l;
        }
    }
}
