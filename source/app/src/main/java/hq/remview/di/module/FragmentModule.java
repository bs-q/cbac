package hq.remview.di.module;

import android.content.Context;

import androidx.core.util.Supplier;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import hq.remview.MVVMApplication;
import hq.remview.ViewModelProviderFactory;
import hq.remview.data.Repository;
import hq.remview.di.scope.FragmentScope;
import hq.remview.ui.base.fragment.BaseFragment;
import hq.remview.ui.main.news.NewsViewModel;
import hq.remview.ui.main.revenue.RevenueViewModel;
import hq.remview.ui.main.setting.SettingViewModel;
import hq.remview.ui.main.store.StoreViewModel;
import hq.remview.utils.GetInfo;

@Module
public class FragmentModule {

    private BaseFragment<?, ?> fragment;

    public FragmentModule(BaseFragment<?, ?> fragment) {
        this.fragment = fragment;
    }

    @Named("access_token")
    @Provides
    @FragmentScope
    String provideToken(Repository repository) {
        return repository.getToken();
    }

    @Named("device_id")
    @Provides
    @FragmentScope
    String provideDeviceId( Context applicationContext){
        return GetInfo.getAll(applicationContext);
    }

    @Provides
    @FragmentScope
    StoreViewModel provideStoreViewModel(Repository repository, Context application) {
        Supplier<StoreViewModel> supplier = () -> new StoreViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<StoreViewModel> factory = new ViewModelProviderFactory<>(StoreViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(StoreViewModel.class);

    }

    @Provides
    @FragmentScope
    RevenueViewModel provideRevenueViewModel(Repository repository, Context application) {
        Supplier<RevenueViewModel> supplier = () -> new RevenueViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<RevenueViewModel> factory = new ViewModelProviderFactory<>(RevenueViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(RevenueViewModel.class);
    }

    @Provides
    @FragmentScope
    NewsViewModel provideNewsViewModel(Repository repository, Context application) {
        Supplier<NewsViewModel> supplier = () -> new NewsViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<NewsViewModel> factory = new ViewModelProviderFactory<>(NewsViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(NewsViewModel.class);
    }
    @Provides
    @FragmentScope
    SettingViewModel provideSettingViewModel(Repository repository, Context application) {
        Supplier<SettingViewModel> supplier = () -> new SettingViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<SettingViewModel> factory = new ViewModelProviderFactory<>(SettingViewModel.class, supplier);
        return new ViewModelProvider(fragment, factory).get(SettingViewModel.class);
    }

}
