package mybar.web.rest.bar.exception;

public class ErrorInfo {

    private String url;
    private String errorMessage;

    public ErrorInfo(String url, String errorMessage) {
        this.url = url;
        this.errorMessage = errorMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}