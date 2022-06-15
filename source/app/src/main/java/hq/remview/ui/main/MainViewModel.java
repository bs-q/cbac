package hq.remview.ui.main;

import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.data.model.api.request.VerifyQRCodeRequest;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.ui.base.activity.BaseCallback;
import hq.remview.ui.base.activity.BaseViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    RestaurantEntity currentRestaurant;
    String token;
    String restaurantId;
    boolean restaurantActive;
    public String restaurantName;

    public MainViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void addRestaurant(RestaurantEntity restaurantEntity){
        compositeDisposable.add(
                repository.getSqliteService().insertRestaurant(restaurantEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }
    public void verifyQrCode(String qrCode,BaseCallback callback){
        VerifyQRCodeRequest request = new VerifyQRCodeRequest();
        request.setQrCode(qrCode);
        request.setDeviceId(deviceId);
        compositeDisposable.add(
                repository.getApiService().verifyQrCode(request)
                        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isResult()) {
                                token = response.getData().getToken();
                                restaurantName = response.getData().getPosName();
                                restaurantId = response.getData().getPosId();
                                restaurantActive = response.getData().isActive();
                                callback.doSuccess();
                            } else {
                                callback.doFail();
                            }
                        }, throwable -> {
                            callback.doError(throwable);
                        }
                )
        );
    }
    public void verifyToken(String token,BaseCallback callback){
        repository.setToken(token);
        compositeDisposable.add(
                repository.getApiService().verifyToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    if (response.isResult()) {
                                        if (response.getData()!= null && response.getData().getToken() != null) {
                                            currentRestaurant.setToken(response.getData().getToken());
                                        }
                                        callback.doSuccess();
                                    } else {
                                        callback.doFail();
                                    }
                                }, callback::doError
                        )
        );
    }
}
