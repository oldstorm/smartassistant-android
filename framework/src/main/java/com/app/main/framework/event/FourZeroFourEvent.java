package com.app.main.framework.event;

public class FourZeroFourEvent {

    private String url;
    private String responseMessage;

    public FourZeroFourEvent() {
    }

    public FourZeroFourEvent(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public FourZeroFourEvent(String url, String responseMessage) {
        this.url = url;
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
