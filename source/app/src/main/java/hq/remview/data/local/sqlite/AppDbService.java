package hq.remview.data.local.sqlite;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.data.model.db.UserEntity;
import io.reactivex.rxjava3.core.Observable;

public class AppDbService implements DbService {

    private final AppDatabase mAppDatabase;

    @Inject
    public AppDbService(AppDatabase appDatabase) {
        this.mAppDatabase = appDatabase;
    }

    @Override
    public Observable<List<UserEntity>> getAllDbUser() {
        return Observable.fromCallable(new Callable<List<UserEntity>>() {
            @Override
            public List<UserEntity> call() throws Exception {
                return mAppDatabase.getDbUserDao().loadAll();
            }
        });
    }

    @Override
    public Observable<Long> insertUser(UserEntity user) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.getDbUserDao().insert(user);
            }
        });
    }

    @Override
    public LiveData<List<UserEntity>> loadAllToLiveData() {
        return mAppDatabase.getDbUserDao().loadAllToLiveData();
    }


    @Override
    public Observable<Boolean> deleteUser(UserEntity user) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.getDbUserDao().delete(user);
                return true;
            }
        });
    }

    @Override
    public Observable<UserEntity> findById(Long id) {
        return Observable.fromCallable(new Callable<UserEntity>() {
            @Override
            public UserEntity call() throws Exception {
               return mAppDatabase.getDbUserDao().findById(id);
            }
        });
    }

    @Override
    public Observable<List<RestaurantEntity>> getAllDbRestaurant() {
        return Observable.fromCallable(new Callable<List<RestaurantEntity>>() {
            @Override
            public List<RestaurantEntity> call() throws Exception {
                return mAppDatabase.getDbRestaurantDao().loadAll();
            }
        });
    }

    @Override
    public LiveData<List<RestaurantEntity>> loadAllRestaurantToLiveData() {
        return mAppDatabase.getDbRestaurantDao().loadAllToLiveData();
    }

    @Override
    public Observable<Long> insertRestaurant(RestaurantEntity restaurant) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mAppDatabase.getDbRestaurantDao().insert(restaurant);
            }
        });
    }

    @Override
    public Observable<Boolean> deleteRestaurant(RestaurantEntity restaurant) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mAppDatabase.getDbRestaurantDao().delete(restaurant);
                return true;
            }
        });
    }

    @Override
    public Observable<RestaurantEntity> findRestaurantById(String id) {
        return Observable.fromCallable(new Callable<RestaurantEntity>() {
            @Override
            public RestaurantEntity call() throws Exception {
                return mAppDatabase.getDbRestaurantDao().findById(id);
            }
        });
    }
}
