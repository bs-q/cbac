package hq.remview.ui.base.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;
import javax.inject.Named;

import hq.remview.MVVMApplication;
import hq.remview.R;
import hq.remview.di.component.DaggerFragmentComponent;
import hq.remview.di.component.FragmentComponent;
import hq.remview.di.module.FragmentModule;
import hq.remview.utils.DialogUtils;
import timber.log.Timber;


public abstract class BaseFragment <B extends ViewDataBinding,V extends BaseFragmentViewModel> extends Fragment {
    @Named("device_id")
    @Inject
    protected String deviceId;

    protected B binding;
    @Inject
    protected V viewModel;

    private Dialog progressDialog;


    @Named("access_token")
    @Inject
    protected String token;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            performDependencyInjection(getBuildComponent());
            binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false);
            binding.setVariable(getBindingVariable(),viewModel);
            performDataBinding();
            viewModel.setToken(token);
            viewModel.mErrorMessage.observe(getViewLifecycleOwner(),toastMessage -> {
                if (toastMessage!=null){
                    toastMessage.showMessage(requireContext());
                }
            });
            viewModel.mIsLoading.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback(){

                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if(((ObservableBoolean)sender).get()){
                        Timber.d("<*>Show progress");
                        showProgressbar(getResources().getString(R.string.msg_loading));
                    }else{
                        Timber.d("<*>Hide progress");
                        hideProgress();
                    }
                }
            });
            view = binding.getRoot();
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public abstract int getBindingVariable();

    protected abstract int getLayoutId();

    protected abstract void performDataBinding();

    protected abstract void performDependencyInjection(FragmentComponent buildComponent);

    private FragmentComponent getBuildComponent(){
        return DaggerFragmentComponent.builder()
                .appComponent(((MVVMApplication) requireActivity().getApplication()).getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    public void showProgressbar(String msg){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = DialogUtils.createDialogLoading(requireContext(), msg);
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public MVVMApplication myApplication(){
        return (MVVMApplication)(requireActivity().getApplication());
    }
}
