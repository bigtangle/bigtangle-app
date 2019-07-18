package net.bigtangle.wallet.core.exception;

public class HttpNetExecuteException extends RuntimeException {

    public HttpNetExecuteException(String toastMessage) {
        this.toastMessage = toastMessage;
    }

    private String toastMessage;

    public String getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String toastMessage) {
        this.toastMessage = toastMessage;
    }
}
