package hq.remview.di.component;

import dagger.Component;
import hq.remview.di.module.ActivityModule;
import hq.remview.di.scope.ActivityScope;
import hq.remview.ui.main.MainActivity;
import hq.remview.ui.main.news.detail.NewsDetailActivity;
import hq.remview.ui.main.revenue.logs.LogFoodActivity;
import hq.remview.ui.main.revenue.revenue.RevenueActivity;
import hq.remview.ui.main.revenue.sell.SellActivity;
import hq.remview.ui.main.revenue.sell.detail.SellDetailActivity;
import hq.remview.ui.main.revenue.statistic.StatisticActivity;
import hq.remview.ui.main.revenue.statistic.pie.PieChartActivity;
import hq.remview.ui.main.setting.employee.EmployeeActivity;
import hq.remview.ui.main.setting.employee.detail.EmployeeDetailActivity;
import hq.remview.ui.main.setting.permission.PermissionActivity;
import hq.remview.ui.main.setting.permission.edit.PermissionEditActivity;

@ActivityScope
@Component(modules = {ActivityModule.class}, dependencies = AppComponent.class)
public interface ActivityComponent {
    void inject(MainActivity activity);

    void inject(RevenueActivity activity);

    void inject(SellActivity activity);

    void inject(SellDetailActivity activity);

    void inject(LogFoodActivity activity);

    void inject(EmployeeActivity activity);

    void inject(NewsDetailActivity activity);

    void inject(EmployeeDetailActivity activity);

    void inject(PermissionActivity activity);

    void inject(PermissionEditActivity activity);

    void inject(StatisticActivity activity);

    void inject(PieChartActivity activity);
}

