package hq.remview.data;

import hq.remview.data.local.prefs.PreferencesService;
import hq.remview.data.local.sqlite.DbService;
import hq.remview.data.remote.ApiService;


public interface Repository {

    /**
     * ################################## Preference section ##################################
     */
    String getToken();
    void setToken(String token);
    PreferencesService getSharedPreferences();


    /**
     * ################################## Sqlite section ##################################
     */
    DbService getSqliteService();



    /**
     *  ################################## Remote api ##################################
     */
    ApiService getApiService();


}
