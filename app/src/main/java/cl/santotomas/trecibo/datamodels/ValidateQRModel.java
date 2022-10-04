package cl.santotomas.trecibo.datamodels;

import java.io.Serializable;

public class ValidateQRModel implements Serializable {

    private String qr;
    private String id;
    private String id_session;
    private String invoice;
    private String purchase_order;
    private String status;
    private String status_code;
    private String number_retries;
    private String amount;
    private String reason;
    private String code_trx;

    public ValidateQRModel(String qr) {
        this.qr = qr;
    }

    public ValidateQRModel(String amount, String reason, String status) {
        this.amount = amount;
        this.reason = reason;
        this.status = status;
    }

    public ValidateQRModel() {
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_session() {
        return id_session;
    }

    public void setId_session(String id_session) {
        this.id_session = id_session;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getPurchase_order() {
        return purchase_order;
    }

    public void setPurchase_order(String purchase_order) {
        this.purchase_order = purchase_order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getNumber_retries() {
        return number_retries;
    }

    public void setNumber_retries(String number_retries) {
        this.number_retries = number_retries;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCode_trx() {
        return code_trx;
    }

    public void setCode_trx(String code_trx) {
        this.code_trx = code_trx;
    }
}
