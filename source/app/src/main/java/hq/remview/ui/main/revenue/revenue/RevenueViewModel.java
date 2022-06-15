package hq.remview.ui.main.revenue.revenue;

import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableLong;

import java.util.List;
import java.util.concurrent.TimeUnit;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.api.obj.DayRevenueUnit;
import hq.remview.ui.base.activity.BaseViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RevenueViewModel extends BaseViewModel {
    public ObservableField<String> restaurantName = new ObservableField<>("Nha hang 1");
    public List<DayRevenueUnit> listReportUnit;
    public ObservableDouble totalMoney = new ObservableDouble(0.0);
    public ObservableLong tick = new ObservableLong(-1);
    public RevenueViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);

    }
    public void startTimer(){
        compositeDisposable.add(Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d->tick.set(tick.get()+1)));
    }
}
