package hq.remview.ui.main.setting.permission.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import hq.remview.R;
import hq.remview.data.model.api.response.permission.PermissionResponse;
import hq.remview.databinding.LayoutPermissionBinding;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionAdapterViewHolder> {
    public interface PermissionClick{
        void PermissionClick(PermissionResponse.Permission permission);
    }
    public PermissionClick callback;
    public PermissionResponse permissionResponse;
    @NonNull
    @Override
    public PermissionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPermissionBinding layoutPermissionBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.layout_permission,parent,false);

        return new PermissionAdapterViewHolder(layoutPermissionBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionAdapterViewHolder holder, int position) {
        holder.layoutPermissionBinding.name.setText(permissionResponse.getDatas().get(position).getName());
        holder.layoutPermissionBinding.getRoot().setOnClickListener((v)-> callback.PermissionClick(permissionResponse.getDatas().get(position)));
        holder.layoutPermissionBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return permissionResponse.getDatas().size();
    }



    public static class PermissionAdapterViewHolder extends RecyclerView.ViewHolder {
       LayoutPermissionBinding layoutPermissionBinding;
        public PermissionAdapterViewHolder(@NonNull LayoutPermissionBinding layoutPermissionBinding) {
            super(layoutPermissionBinding.getRoot());
            this.layoutPermissionBinding = layoutPermissionBinding;

        }
    }
}
