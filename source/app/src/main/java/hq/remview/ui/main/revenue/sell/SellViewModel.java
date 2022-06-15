package hq.remview.ui.main.revenue.sell;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.ui.base.activity.BaseViewModel;

public class SellViewModel extends BaseViewModel {
    List<BillingItemUnit> sellList = new ArrayList<>();
    public ObservableField<String> restaurantName = new ObservableField<>("Nha hang 1");

    public SellViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);

    }
}
