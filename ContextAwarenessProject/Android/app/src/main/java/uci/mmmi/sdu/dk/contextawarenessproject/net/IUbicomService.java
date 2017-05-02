package uci.mmmi.sdu.dk.contextawarenessproject.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import uci.mmmi.sdu.dk.contextawarenessproject.entities.DeviceStatus;

/**
 * Created by peter on 28-04-17.
 */
public interface IUbicomService {
    @POST("board/device/{deviceId}")
    Call<DeviceStatus> sendDeviceStatus(@Path("deviceId") String deviceId, @Body DeviceStatus status);

    @GET("board/data")
    Call<List<DeviceStatus>> getAllDeviceStatuses();
}
