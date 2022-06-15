package hq.remview.ui.main.revenue.sell.detail;

import androidx.databinding.ObservableField;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.api.obj.BillingItemUnit;
import hq.remview.ui.base.activity.BaseViewModel;

public class SellDetailViewModel extends BaseViewModel {
    BillingItemUnit billingItemUnit;
    public ObservableField<String> restaurantName = new ObservableField<>("Nha hang 1");

    public SellDetailViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);

    }
}
