package hq.remview.ui.main.setting.permission.edit.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import hq.remview.R;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import hq.remview.databinding.LayoutPermissionSelectBinding;
import hq.remview.utils.AppUtils;

public class PermissionEditAdapter extends RecyclerView.Adapter<PermissionEditAdapter.PermissionAdapterViewHolder> {
    Set<Integer> permission;
    public List<PermissionToggleItem> permissionToggleItemList = new ArrayList<>();
    public void createPermission(Integer range){
        permission = AppUtils.getPermissionSet(range);
    }
    public static class PermissionToggleItem {
        public Integer base;
        public Integer name;
        public ObservableBoolean check = new ObservableBoolean(false);

        public PermissionToggleItem(Integer base,Integer name){
            this.base = base;
            this.name = name;
        }
    }
    public void createPermissionList(){
        PermissionToggleItem four = new PermissionToggleItem(4,R.string.four);
        PermissionToggleItem sixfour = new PermissionToggleItem(64,R.string.sixfour);
        PermissionToggleItem onetwoeight = new PermissionToggleItem(128,R.string.onetwoeight);
        PermissionToggleItem twofivesix = new PermissionToggleItem(256,R.string.twofivesix);
        PermissionToggleItem fiveonetwo = new PermissionToggleItem(512,R.string.fiveonetwo);
        PermissionToggleItem onezerotwofour = new PermissionToggleItem(1024,R.string.onezerotwofour);
        PermissionToggleItem two = new PermissionToggleItem(2,R.string.two);
        PermissionToggleItem twozerofoureight = new PermissionToggleItem(2048,R.string.twozerofoureight);
        PermissionToggleItem threetwo = new PermissionToggleItem(32,R.string.threetwo);
        PermissionToggleItem fourzeroninesix = new PermissionToggleItem(4096,R.string.fourzeroninesix);
        PermissionToggleItem eight = new PermissionToggleItem(8,R.string.eight);
        PermissionToggleItem onesix = new PermissionToggleItem(16,R.string.onesix);
        PermissionToggleItem one = new PermissionToggleItem(1,R.string.one);
        PermissionToggleItem eightoneninetwo = new PermissionToggleItem(8192,R.string.eightoneninetwo);

        permissionToggleItemList.add(four);
        permissionToggleItemList.add(sixfour);
        permissionToggleItemList.add(onetwoeight);
        permissionToggleItemList.add(one);
        permissionToggleItemList.add(twofivesix);
        permissionToggleItemList.add(fiveonetwo);
        permissionToggleItemList.add(onezerotwofour);
        permissionToggleItemList.add(two);
        permissionToggleItemList.add(twozerofoureight);
        permissionToggleItemList.add(threetwo);
        permissionToggleItemList.add(fourzeroninesix);
        permissionToggleItemList.add(eight);
        permissionToggleItemList.add(onesix);
        permissionToggleItemList.add(eightoneninetwo);

        for (PermissionToggleItem i : permissionToggleItemList) {
            if (permission.contains(i.base)) {
                i.check.set(true);
            }
        }
    }
    @NonNull
    @Override
    public PermissionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPermissionSelectBinding layoutPermissionSelectBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.layout_permission_select,parent,false);

        return new PermissionAdapterViewHolder(layoutPermissionSelectBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionAdapterViewHolder holder, int position) {
        holder.layoutPermissionSelectBinding.setName(holder.itemView.getContext().getString(permissionToggleItemList.get(position).name));

        holder.layoutPermissionSelectBinding.getRoot().setOnClickListener((v)->{
            permissionToggleItemList.get(position).check.set(!permissionToggleItemList.get(position).check.get());
        });
        holder.layoutPermissionSelectBinding.setCheck( permissionToggleItemList.get(position).check);
        holder.layoutPermissionSelectBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return permissionToggleItemList.size();
    }



    public static class PermissionAdapterViewHolder extends RecyclerView.ViewHolder {
       LayoutPermissionSelectBinding layoutPermissionSelectBinding;
        public PermissionAdapterViewHolder(@NonNull LayoutPermissionSelectBinding layoutPermissionSelectBinding) {
            super(layoutPermissionSelectBinding.getRoot());
            this.layoutPermissionSelectBinding = layoutPermissionSelectBinding;

        }
    }
}
