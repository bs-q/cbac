package hq.remview.data.local.sqlite;

import androidx.lifecycle.LiveData;

import java.util.List;

import hq.remview.data.model.db.RestaurantEntity;
import hq.remview.data.model.db.UserEntity;
import io.reactivex.rxjava3.core.Observable;

public interface DbService {

    Observable<List<UserEntity>> getAllDbUser();

    LiveData<List<UserEntity>> loadAllToLiveData();

    Observable<Long> insertUser(UserEntity user);

    Observable<Boolean> deleteUser(UserEntity user);

    Observable<UserEntity> findById(Long id);

    Observable<List<RestaurantEntity>> getAllDbRestaurant();

    LiveData<List<RestaurantEntity>> loadAllRestaurantToLiveData();

    Observable<Long> insertRestaurant(RestaurantEntity restaurant);

    Observable<Boolean> deleteRestaurant(RestaurantEntity restaurant);

    Observable<RestaurantEntity> findRestaurantById(String id);

}
