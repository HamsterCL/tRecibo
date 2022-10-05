package cl.santotomas.trecibo.datamodels;

import java.sql.Date;

public class AccountModel {

    private int id;
    private String id_unique;
    private String first_name;
    private String last_name;
    private int rut;
    private String dv;
    private String address_1;
    private String address_2;
    private int phone ;
    private String email;
    private String avatar;
    private boolean banned;
    private boolean deleted;
    private int postpaid;
    private String created_at;
    private String updated_at;
    private boolean fingerprint;

    public AccountModel() {

    }

    public AccountModel(String first_name, String last_name, int phone, String id_unique) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.id_unique = id_unique;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_unique() {
        return id_unique;
    }

    public void setId_unique(String id_unique) {
        this.id_unique = id_unique;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getRut() {
        return rut;
    }

    public void setRut(int rut) {
        this.rut = rut;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getPostpaid() {
        return postpaid;
    }

    public void setPostpaid(int postpaid) {
        this.postpaid = postpaid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(boolean fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return "AccountModel{" +
                "id=" + id +
                ", id_unique='" + id_unique + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", rut=" + rut +
                ", dv='" + dv + '\'' +
                ", address_1='" + address_1 + '\'' +
                ", address_2='" + address_2 + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", banned=" + banned +
                ", deleted=" + deleted +
                ", postpaid=" + postpaid +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", fingerprint=" + fingerprint +
                '}';
    }
}
