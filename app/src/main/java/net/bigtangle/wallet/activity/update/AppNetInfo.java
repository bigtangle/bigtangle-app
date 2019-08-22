package net.bigtangle.wallet.activity.update;

public class AppNetInfo {
    // app 名称
    private String versionName;

    // app 描述
    private String versionDes;

    // app 下载地址
    private String downloadUrl;

    // app 版号号
    private int versionCode;

    public AppNetInfo() {
    }

    public AppNetInfo(String versionName, String versionDes, String downloadUrl, int versionCode) {
        this.versionName = versionName;
        this.versionDes = versionDes;
        this.downloadUrl = downloadUrl;
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionDes() {
        return versionDes;
    }

    public void setVersionDes(String versionDes) {
        this.versionDes = versionDes;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String toString() {
        return "{versionName=" + versionName + ";" +
                "versionCode=" + versionCode + ";" +
                "versionDes=" + versionDes + ";" +
                "downloadUrl=" + downloadUrl + "}";
    }
}
