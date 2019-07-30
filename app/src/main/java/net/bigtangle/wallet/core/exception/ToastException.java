package net.bigtangle.wallet.core.exception;

public class ToastException extends RuntimeException {

    public ToastException(String toastMessage) {
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
