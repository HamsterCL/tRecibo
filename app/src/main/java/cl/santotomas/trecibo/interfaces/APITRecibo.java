package cl.santotomas.trecibo.interfaces;

import cl.santotomas.trecibo.datamodels.CreateQRModel;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APITRecibo {

    @Headers("Content-Type: application/json")
    @POST("/static/core/api/payment/qr/create")
    Call<CreateQRModel> postCreateQR(@Header("Authorization") String token, @Body CreateQRModel createQRModel);

    @Headers("Content-Type: application/json")
    @POST("/static/core/api/payment/qr/validate")
    Call<ValidateQRModel> validateCreateQR(@Header("Authorization") String token, @Body ValidateQRModel validateQRModel);
}
