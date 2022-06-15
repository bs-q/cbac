package hq.remview.ui.main.revenue;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.ui.base.fragment.BaseFragmentViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Setter;
import timber.log.Timber;

public class RevenueViewModel extends BaseFragmentViewModel {
    protected final ObservableBoolean detailSelected = new ObservableBoolean();
    public ObservableField<String> restaurantName = new ObservableField<>("");

    // check connection flag
    public ObservableBoolean connection = new ObservableBoolean(true);
    @Setter
    RestaurantEntity currentRestaurant = null;
    public void deleteRestaurant(RestaurantEntity entity){
        compositeDisposable.add(repository.getSqliteService().deleteRestaurant(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    Timber.d("deleteRestaurant: success");
                },throwable -> {
                    Timber.d("deleteRestaurant: failed");
                }));
    }
    public void addRestaurant(RestaurantEntity entity){
        compositeDisposable.add(repository.getSqliteService().insertRestaurant(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Timber.d("addRestaurant: success");
                    showSuccessMessage("add restaurant success");
                },throwable -> {
                    Timber.d("addRestaurant: failed");
                    showErrorMessage("failed to add restaurant");
                }));
    }
    public RevenueViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void setDetailSelected(boolean selected){
        detailSelected.set(selected);
    }

}
