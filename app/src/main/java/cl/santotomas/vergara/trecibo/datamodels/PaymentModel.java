package cl.santotomas.vergara.trecibo.datamodels;

import java.io.Serializable;

public class PaymentModel implements Serializable {

    private String id;
    private String user;
    private String amount;
    private String date;
    private String reason;
    private String status;
    private String authorization_code;

    public PaymentModel() {

    }

    @Override
    public String toString() {
        return "PaymentModel{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", amount='" + amount + '\'' +
                ", date='" + date + '\'' +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", authorization_code='" + authorization_code + '\'' +
                '}';
    }

    public PaymentModel(String id, String user, String amount, String date, String reason, String status, String authorization_code) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.status = status;
        this.authorization_code = authorization_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthorization_code() {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code) {
        this.authorization_code = authorization_code;
    }
}



