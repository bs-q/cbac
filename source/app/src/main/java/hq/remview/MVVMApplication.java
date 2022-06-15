package hq.remview;

import android.app.Application;
import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import es.dmoral.toasty.Toasty;
import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.di.component.AppComponent;
import hq.remview.di.component.DaggerAppComponent;
import hq.remview.others.MyTimberDebugTree;
import hq.remview.others.MyTimberReleaseTree;
import hq.remview.utils.DialogUtils;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public class MVVMApplication extends Application{
    @Setter
    private AppCompatActivity currentActivity;

    @Getter
    private AppComponent appComponent;

    @Setter
    @Getter
    private RestaurantEntity currentRestaurant;

    public ObservableField<String> currentRestaurantName = new ObservableField<>("");

    public void setCurrentRestaurant(RestaurantEntity restaurant){
        this.currentRestaurant = restaurant;
        currentRestaurantName.set(restaurant.name);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Enable firebase log
        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);


        if (BuildConfig.DEBUG) {
            Timber.plant(new MyTimberDebugTree());
        }else{
            Timber.plant(new MyTimberReleaseTree(firebaseCrashlytics));
        }

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);

        // Init Toasty
        Toasty.Config.getInstance()
                .allowQueue(false)
                .apply();


        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleObserver() {

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            void onMoveToForeground(){
                Timber.d("-----------------> foreground");
                WebSocketLiveData.getInstance().setAppOnline(true);
                if (currentRestaurant!=null && currentRestaurant.getToken()!=null){
                    Timber.d("===> set token");
                    WebSocketLiveData.getInstance().setSession(currentRestaurant.getToken());
                }
                WebSocketLiveData.getInstance().startSocket();
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            void onMoveToBackground(){
                Timber.d("-----------------> background");
                WebSocketLiveData.getInstance().setAppOnline(false);
                WebSocketLiveData.getInstance().stopSocket();
            }
        });
//        networkDisposable.add( ReactiveNetwork
//                .observeNetworkConnectivity(this)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(connectivity -> {
//                    switch (connectivity.state()){
//
//                        case CONNECTING:
//                            break;
//                        case CONNECTED:
//                            break;
//                        case SUSPENDED:
//                            break;
//                        case DISCONNECTING:
//                            break;
//                        case DISCONNECTED:
//                            break;
//                        case UNKNOWN:
//                            break;
//                    }
//                }));
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    public PublishSubject<Integer> showDialogNoInternetAccess(){
        Timber.d("No wifi");
        final PublishSubject<Integer> subject = PublishSubject.create();
        currentActivity.runOnUiThread(() ->
                {
                    Dialog dialog =  DialogUtils.dialogConfirm(currentActivity, currentActivity.getResources().getString(R.string.newtwork_error),
                            null,
                            null, currentActivity.getResources().getString(R.string.newtwork_error_button_exit),
                            (dialogInterface, i) -> android.os.Process.killProcess(android.os.Process.myPid()));
                    compositeDisposable.add( subject.doOnComplete(() -> {
                        if (dialog!=null){
                            dialog.dismiss();
                        }
                    }).subscribe(retry -> {}));
                }

        );
        return subject;
    }
}
