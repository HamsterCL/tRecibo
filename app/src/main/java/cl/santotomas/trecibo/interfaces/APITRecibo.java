package cl.santotomas.trecibo.interfaces;

import java.util.List;

import cl.santotomas.trecibo.datamodels.AccountModel;
import cl.santotomas.trecibo.datamodels.CreateQRModel;
import cl.santotomas.trecibo.datamodels.PaymentModel;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APITRecibo {

    @Headers("Content-Type: application/json")
    //@POST("/static/core/api/account/register")
    @POST("/api/account/register")
    Call<AccountModel> postCreateAccount(@Body AccountModel accountModel);

    @Headers("Content-Type: application/json")
    @POST("/static/core/api/payment/qr/create")
    Call<CreateQRModel> postCreateQR(@Header("Authorization") String token, @Body CreateQRModel createQRModel);

    @Headers("Content-Type: application/json")
    @POST("/static/core/api/payment/qr/validate")
    Call<ValidateQRModel> validateCreateQR(@Header("Authorization") String token, @Body ValidateQRModel validateQRModel);

    @Headers("Content-Type: application/json")
    @GET("/static/core/api/payment/invoice/search/month")
    Call<List<PaymentModel>> paymentsMonth(@Header("Authorization") String token);
}
