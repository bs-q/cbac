package hq.remview.ui.main.revenue.logs;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.api.obj.LogFoodItem;
import hq.remview.ui.base.activity.BaseViewModel;

public class LogFoodViewModel extends BaseViewModel {
    public ObservableField<String> restaurantName = new ObservableField<>("Nha hang 1");
    public List<LogFoodItem> dataLogFood = new ArrayList<>();

    public LogFoodViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
}
