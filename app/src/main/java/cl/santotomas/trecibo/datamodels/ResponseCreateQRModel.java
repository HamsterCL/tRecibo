package cl.santotomas.trecibo.datamodels;

public class ResponseCreateQRModel {

    String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ResponseCreateQRModel(String data) {
        this.data = data;
    }

    public ResponseCreateQRModel() {
    }
}
