package hq.remview.ui.main.store;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import java.util.List;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.local.sqlite.DbService;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.ui.base.fragment.BaseFragmentViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class StoreViewModel extends BaseFragmentViewModel {
    public ObservableField<String> restaurantName = new ObservableField<>("");
    LiveData<List<RestaurantEntity>> restaurantEntityLiveData ;
    DbService dbService;

    protected final ObservableBoolean restaurantSelected = new ObservableBoolean();
    protected final ObservableBoolean detailSelected = new ObservableBoolean();
    RestaurantEntity currentRestaurant = null;
    // check connection flag
    public ObservableBoolean connection = new ObservableBoolean(true);
    public StoreViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        dbService = repository.getSqliteService();
        restaurantEntityLiveData=dbService.loadAllRestaurantToLiveData();
    }
    public void addRestaurant(RestaurantEntity entity){
        compositeDisposable.add(dbService.insertRestaurant(entity)
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
    public void deleteRestaurant(RestaurantEntity entity){
        compositeDisposable.add(dbService.deleteRestaurant(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    Timber.d("deleteRestaurant: success");
                },throwable -> {
                    Timber.d("deleteRestaurant: failed");
                }));
    }

    public void setRestaurantSelected(boolean selected){
        restaurantSelected.set(selected);
    }

    public void setDetailSelected(boolean selected){
        detailSelected.set(selected);
    }
    public void updateLang(String locale){
        repository.getSharedPreferences().setCurrentLanguage(locale);
    }
    public String getLang(){
        return repository.getSharedPreferences().getCurrentLanguage();
    }
}
