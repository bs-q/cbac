package hq.remview.di.component;


import dagger.Component;
import hq.remview.di.module.FragmentModule;
import hq.remview.di.scope.FragmentScope;
import hq.remview.ui.main.news.NewsFragment;
import hq.remview.ui.main.revenue.RevenueFragment;
import hq.remview.ui.main.setting.SettingFragment;
import hq.remview.ui.main.store.StoreFragment;

@FragmentScope
@Component(modules = {FragmentModule.class},dependencies = AppComponent.class)
public interface FragmentComponent {
    void inject(RevenueFragment fragment);

    void inject(StoreFragment fragment);

    void inject(NewsFragment fragment);

    void inject(SettingFragment fragment);
}
