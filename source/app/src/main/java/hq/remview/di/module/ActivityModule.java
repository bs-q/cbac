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
import hq.remview.di.scope.ActivityScope;
import hq.remview.ui.base.activity.BaseActivity;
import hq.remview.ui.main.MainViewModel;
import hq.remview.ui.main.news.detail.NewsDetailViewModel;
import hq.remview.ui.main.revenue.logs.LogFoodViewModel;
import hq.remview.ui.main.revenue.revenue.RevenueViewModel;
import hq.remview.ui.main.revenue.sell.SellViewModel;
import hq.remview.ui.main.revenue.sell.detail.SellDetailViewModel;
import hq.remview.ui.main.revenue.statistic.StatisticViewModel;
import hq.remview.ui.main.revenue.statistic.pie.PieChartViewModel;
import hq.remview.ui.main.setting.employee.EmployeeViewModel;
import hq.remview.ui.main.setting.employee.detail.EmployeeDetailViewModel;
import hq.remview.ui.main.setting.permission.PermissionViewModel;
import hq.remview.ui.main.setting.permission.edit.PermissionEditViewModel;
import hq.remview.utils.GetInfo;

@Module
public class ActivityModule {

    private BaseActivity<?, ?> activity;

    public ActivityModule(BaseActivity<?, ?> activity) {
        this.activity = activity;
    }

    @Named("access_token")
    @Provides
    @ActivityScope
    String provideToken(Repository repository){
        return repository.getToken();
    }

    @Named("device_id")
    @Provides
    @ActivityScope
    String provideDeviceId( Context applicationContext){
        return GetInfo.getAll(applicationContext);
    }


    @Provides
    @ActivityScope
    MainViewModel provideMainViewModel(Repository repository, Context application) {
        Supplier<MainViewModel> supplier = () -> new MainViewModel(repository, (MVVMApplication)application);
        ViewModelProviderFactory<MainViewModel> factory = new ViewModelProviderFactory<>(MainViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(MainViewModel.class);
    }

    @Provides
    @ActivityScope
    RevenueViewModel provideRevenueViewModel(Repository repository, Context application) {
        Supplier<RevenueViewModel> supplier = () -> new RevenueViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<RevenueViewModel> factory = new ViewModelProviderFactory<>(RevenueViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(RevenueViewModel.class);
    }

    @Provides
    @ActivityScope
    SellViewModel provideSellViewModel(Repository repository, Context application) {
        Supplier<SellViewModel> supplier = () -> new SellViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<SellViewModel> factory = new ViewModelProviderFactory<>(SellViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SellViewModel.class);
    }

    @Provides
    @ActivityScope
    SellDetailViewModel provideSellDetailViewModel(Repository repository, Context application) {
        Supplier<SellDetailViewModel> supplier = () -> new SellDetailViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<SellDetailViewModel> factory = new ViewModelProviderFactory<>(SellDetailViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(SellDetailViewModel.class);
    }

    @Provides
    @ActivityScope
    LogFoodViewModel provideLogFoodViewModel(Repository repository, Context application) {
        Supplier<LogFoodViewModel> supplier = () -> new LogFoodViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<LogFoodViewModel> factory = new ViewModelProviderFactory<>(LogFoodViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(LogFoodViewModel.class);
    }

    @Provides
    @ActivityScope
    EmployeeViewModel provideEmployeeViewModel(Repository repository, Context application) {
        Supplier<EmployeeViewModel> supplier = () -> new EmployeeViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<EmployeeViewModel> factory = new ViewModelProviderFactory<>(EmployeeViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(EmployeeViewModel.class);
    }

    @Provides
    @ActivityScope
    NewsDetailViewModel provideNewsDetailViewModel(Repository repository, Context application) {
        Supplier<NewsDetailViewModel> supplier = () -> new NewsDetailViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<NewsDetailViewModel> factory = new ViewModelProviderFactory<>(NewsDetailViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(NewsDetailViewModel.class);
    }

    @Provides
    @ActivityScope
    EmployeeDetailViewModel provideEmployeeDetailViewModel(Repository repository, Context application) {
        Supplier<EmployeeDetailViewModel> supplier = () -> new EmployeeDetailViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<EmployeeDetailViewModel> factory = new ViewModelProviderFactory<>(EmployeeDetailViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(EmployeeDetailViewModel.class);
    }

    @Provides
    @ActivityScope
    PermissionViewModel providePermissionViewModel(Repository repository, Context application) {
        Supplier<PermissionViewModel> supplier = () -> new PermissionViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<PermissionViewModel> factory = new ViewModelProviderFactory<>(PermissionViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(PermissionViewModel.class);
    }

    @Provides
    @ActivityScope
    PermissionEditViewModel providePermissionEditViewModel(Repository repository, Context application) {
        Supplier<PermissionEditViewModel> supplier = () -> new PermissionEditViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<PermissionEditViewModel> factory = new ViewModelProviderFactory<>(PermissionEditViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(PermissionEditViewModel.class);
    }

    @Provides
    @ActivityScope
    StatisticViewModel provideStatisticViewModel(Repository repository, Context application) {
        Supplier<StatisticViewModel> supplier = () -> new StatisticViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<StatisticViewModel> factory = new ViewModelProviderFactory<>(StatisticViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(StatisticViewModel.class);
    }

    @Provides
    @ActivityScope
    PieChartViewModel providePieChartViewModel(Repository repository, Context application) {
        Supplier<PieChartViewModel> supplier = () -> new PieChartViewModel(repository, (MVVMApplication) application);
        ViewModelProviderFactory<PieChartViewModel> factory = new ViewModelProviderFactory<>(PieChartViewModel.class, supplier);
        return new ViewModelProvider(activity, factory).get(PieChartViewModel.class);
    }
}
