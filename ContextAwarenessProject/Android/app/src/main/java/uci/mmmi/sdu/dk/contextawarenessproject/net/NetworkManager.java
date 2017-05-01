package uci.mmmi.sdu.dk.contextawarenessproject.net;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by peter on 28-04-17.
 */

public class NetworkManager {

    private static final String URL = "https://ubicom.herokuapp.com/";

    private static NetworkManager instance;

    private IUbicomService ubicomService;

    public NetworkManager(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ubicomService = retrofit.create(IUbicomService.class);
    }

    public IUbicomService getUbicomService() {
        return ubicomService;
    }


    public static NetworkManager getInstance(Context context) {
        if(instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }
}
