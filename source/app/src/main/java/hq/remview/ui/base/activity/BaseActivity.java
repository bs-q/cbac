package hq.remview.ui.base.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.constant.Constants;
import hq.remview.data.local.prefs.PreferencesService;
import hq.remview.data.websocket.Message;
import hq.remview.data.websocket.SocketEventModel;
import hq.remview.data.websocket.SocketListener;
import hq.remview.data.websocket.WebSocketLiveData;
import hq.remview.di.component.ActivityComponent;
import hq.remview.di.component.DaggerActivityComponent;
import hq.remview.di.module.ActivityModule;
import hq.remview.utils.DeviceUtils;
import hq.remview.utils.DialogUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;

public abstract class BaseActivity<B extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity implements SocketListener {

    protected B viewBinding;

    @Inject
    protected V viewModel;

    @Inject
    protected Context application;

    @Named("access_token")
    @Inject
    protected String token;

    @Named("device_id")
    @Inject
    protected String deviceId;

    private Dialog progressDialog;

    // Listen all action from local
    private BroadcastReceiver globalApplicationReceiver;
    private IntentFilter filterGlobalApplication;
    PublishSubject<Integer> networkDialog;

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //handle status listener
            String action = intent.getAction();

            //wifi
            if (action != null && (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||
                    action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)||
                    action.equals(WifiManager.RSSI_CHANGED_ACTION))) {
                setupWifiLevel();
            }
        }

    };
    NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .build();
    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            if (DeviceUtils.isNetworkAvailable(BaseActivity.this)) {
                Timber.d("On available");
                if (networkDialog!=null && !networkDialog.hasComplete()) {
                    networkDialog.onComplete();
                    networkDialog = null;
                }
            }

        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            if (!DeviceUtils.isNetworkAvailable(BaseActivity.this)){
                Timber.d("On lost");
                if (networkDialog!=null && !networkDialog.hasComplete()) return;
                networkDialog = ((MVVMApplication)application).showDialogNoInternetAccess();
            }

        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
//            Timber.d("On Unavailable");
//            if (networkDialog!=null && !networkDialog.hasComplete()) return;
//            networkDialog = ((MVVMApplication)application).showDialogNoInternetAccess();
        }
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Timber.d("On capabilities changed");
        }
    };
    private void registerStatusBarService() {
        IntentFilter filter;
        filter = new IntentFilter();

        //wifi
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        //bluetooth
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        //air plan mode
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);


        this.registerReceiver(statusReceiver, filter);

    }
    private void setupWifiLevel() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int numberOfLevels = 5;
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if (wifiManager.isWifiEnabled()) {
                if (DeviceUtils.isNetworkAvailable(this)) {
//                    if (networkDialog!=null && !networkDialog.hasComplete()) {
//                        networkDialog.onComplete();
//                        networkDialog = null;
//                    }
                } else {
//                    if (networkDialog!=null && !networkDialog.hasComplete()) return;
//                    networkDialog = ((MVVMApplication)application).showDialogNoInternetAccess();
                }

            } else if (!wifiManager.isWifiEnabled()) {
//                if (networkDialog!=null && !networkDialog.hasComplete()) return;
//                networkDialog = ((MVVMApplication)application).showDialogNoInternetAccess();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences mPrefs= newBase.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        newBase.getResources().getConfiguration().setLocale(new Locale(mPrefs.getString(PreferencesService.LANG,"vi")));
        this.applyOverrideConfiguration(newBase.getResources().getConfiguration());
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection(getBuildComponent());
        super.onCreate(savedInstanceState);
        performDataBinding();
        updateCurrentAcitivity();

        viewModel.setToken(token);
        viewModel.setDeviceId(deviceId);
        viewModel.mIsLoading.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback(){

            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(((ObservableBoolean)sender).get()){
                    showProgressbar(getResources().getString(R.string.msg_loading));
                }else{
                    hideProgress();
                }
            }
        });
        viewModel.mErrorMessage.observe(this, toastMessage -> {
            if(toastMessage!=null){
                toastMessage.showMessage(getApplicationContext());
            }
        });

        filterGlobalApplication = new IntentFilter();
        filterGlobalApplication.addAction(Constants.ACTION_EXPIRED_TOKEN);
        globalApplicationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action==null){
                    return;
                }
                if (action.equals(Constants.ACTION_EXPIRED_TOKEN)){
                    doExpireSession();
                }
            }
        };
//        registerStatusBarService();
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void checkConnectionWhenOpen() {
        if (DeviceUtils.isNetworkAvailable(this)) {
            if (networkDialog != null && !networkDialog.hasComplete()) {
                networkDialog.onComplete();
                networkDialog = null;
            }
        } else {
            if (networkDialog != null && !networkDialog.hasComplete()) return;
            networkDialog = ((MVVMApplication) application).showDialogNoInternetAccess();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        WebSocketLiveData.getInstance().setSocketListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(globalApplicationReceiver, filterGlobalApplication);
        updateCurrentAcitivity();
        checkConnectionWhenOpen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(globalApplicationReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(statusReceiver);
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(ConnectivityManager.class);
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    public abstract @LayoutRes int getLayoutId();

    public abstract int getBindingVariable();

    public void doExpireSession() {
        //implement later

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void performDataBinding() {
        viewBinding = DataBindingUtil.setContentView(this, getLayoutId());
        viewBinding.setVariable(getBindingVariable(), viewModel);
        viewBinding.executePendingBindings();
    }

    public void showProgressbar(String msg){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = DialogUtils.createDialogLoading(this, msg);
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private ActivityComponent getBuildComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(((MVVMApplication)getApplication()).getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    public abstract void performDependencyInjection(ActivityComponent buildComponent);

    private void updateCurrentAcitivity(){
        MVVMApplication mvvmApplication = (MVVMApplication)application;
        mvvmApplication.setCurrentActivity(this);
    }


    /**
     *
     * Socket event listener
     *
     * */

    @Override
    public void onMessage(SocketEventModel socketEventModel) {

    }

    @Override
    public void onTimeout(Message message) {
        runOnUiThread(() -> {
            viewModel.hideLoading();
        });
    }

    @Override
    public void onError() {
        viewModel.notifySocketError();
        Timber.d("socket error");
    }

    @Override
    public void onConnected() {
        runOnUiThread(() -> {

        });
    }
    public void listenToSocketConnection(){
        viewModel.compositeDisposable.add( viewModel.socketWatcher.throttleFirst(5, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        value -> {
                            Timber.d("show");
                            viewModel.hideLoading();
                            viewModel.showErrorMessage(getString(R.string.can_not_connect_to_server));
                        }

                ));

    }
    public MVVMApplication myApplication(){
        return (MVVMApplication)(getApplication());
    }
}
