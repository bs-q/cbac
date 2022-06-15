package hq.remview.ui.main.setting.employee;

import androidx.databinding.ObservableLong;

import java.util.concurrent.TimeUnit;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.ui.base.activity.BaseCallback;
import hq.remview.ui.base.activity.BaseViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EmployeeViewModel extends BaseViewModel {
    String currentEmployee;
    public ObservableLong tick = new ObservableLong(-1);

    public EmployeeViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void startTimer(){
        compositeDisposable.add(Observable.timer(15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(d->tick.set(tick.get()+1)));
    }
    public void disposeBag(){
        compositeDisposable.clear();
    }
}
