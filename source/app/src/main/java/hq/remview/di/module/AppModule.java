package hq.remview.di.module;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hq.remview.BuildConfig;
import hq.remview.constant.Constants;
import hq.remview.data.AppRepository;
import hq.remview.data.Repository;
import hq.remview.data.local.prefs.AppPreferencesService;
import hq.remview.data.local.prefs.PreferencesService;
import hq.remview.data.local.sqlite.AppDatabase;
import hq.remview.data.local.sqlite.AppDbService;
import hq.remview.data.local.sqlite.DbService;
import hq.remview.data.remote.ApiService;
import hq.remview.data.remote.AuthInterceptor;
import hq.remview.di.qualifier.ApiInfo;
import hq.remview.di.qualifier.DatabaseInfo;
import hq.remview.di.qualifier.PreferenceInfo;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    @Provides
    @ApiInfo
    @Singleton
    String provideBaseUrl() {
        return BuildConfig.BASE_URL;
    }

    @Provides
    @DatabaseInfo
    @Singleton
    String provideDatabaseName() {
        return Constants.DB_NAME;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@DatabaseInfo String dbName, Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, dbName).fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    DbService provideDbService(AppDbService appDbService) {
        return appDbService;
    }


    @Provides
    @PreferenceInfo
    @Singleton
    String providePreferenceName() {
        return Constants.PREF_NAME;
    }


    @Provides
    @Singleton
    PreferencesService providePreferencesService(AppPreferencesService appPreferencesService) {
        return appPreferencesService;
    }

    @Provides
    @Singleton
    AuthInterceptor proviceAuthInterceptor(PreferencesService appPreferencesService, Application application){
        return new AuthInterceptor(appPreferencesService, application);
    }

    // Config OK HTTP
    @Provides
    @Singleton
    public OkHttpClient getClient(AuthInterceptor authInterceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.level(HttpLoggingInterceptor.Level.NONE);
        }
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    // Create Retrofit
    @Provides
    @Singleton
    public Retrofit retrofitBuild(OkHttpClient client, @ApiInfo String url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    // Create api service
    @Provides
    @Singleton
    public ApiService apiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }


    @Provides
    @Singleton
    public Repository provideDataManager(AppRepository appRepository) {
        return appRepository;
    }

}
