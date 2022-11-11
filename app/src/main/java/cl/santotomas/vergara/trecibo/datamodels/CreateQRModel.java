package cl.santotomas.vergara.trecibo.datamodels;

public class CreateQRModel {

    private String amount;
    private String reason;
    private String data;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

    public CreateQRModel(String amount, String reason) {
        this.amount = amount;
        this.reason = reason;
    }
}
