package hq.remview.ui.main.setting.employee.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.data.model.api.response.employee.EmployeeResponse;
import hq.remview.databinding.LayoutEmployeeMoneyBinding;
import hq.remview.utils.AppUtils;
import lombok.Setter;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {
    public interface EmployeeAdapterCallback{
        void detail(EmployeeResponse.EmployeObject employeeObject);
    }
    @Setter
    EmployeeAdapterCallback callback;
    public MVVMApplication application;
    @Setter
    EmployeeResponse response;

    public void clearData(){
        if (response!=null && response.getEmployees()!=null){
            response.employees.clear();
            notifyDataSetChanged();
        }
    }
    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutEmployeeMoneyBinding l = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_employee_money,parent,false);
        return new EmployeeViewHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        holder.l.setName(response.employees.get(position).employeeName);
        double sum = response.employees.get(position).money.stream().mapToDouble(EmployeeResponse.Money::getMoney).sum()/100;
        holder.l.setMoney(AppUtils.formatDoubleMoneyHasCurrency(sum,
                application.getCurrentRestaurant().getSetting()));
        holder.l.getRoot().setOnClickListener(v->callback.detail(response.employees.get(position)));
        holder.l.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return response.employees.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        LayoutEmployeeMoneyBinding l;
        public EmployeeViewHolder(@NonNull LayoutEmployeeMoneyBinding l) {
            super(l.getRoot());
            this.l = l;
        }
    }
}
