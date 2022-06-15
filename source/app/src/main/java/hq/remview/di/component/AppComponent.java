package hq.remview.di.component;


import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import hq.remview.MVVMApplication;
import hq.remview.data.Repository;
import hq.remview.di.module.AppModule;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MVVMApplication app);

    Repository getRepository();

    Context getContext();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
