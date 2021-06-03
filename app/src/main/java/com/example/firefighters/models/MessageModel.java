package com.example.firefighters.models;

import java.util.ArrayList;

public class MessageModel {

    private String message;
    private String imagesSrc;
    private String videoSrc;
    private String audioSrc;

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagesSrc() {
        return imagesSrc;
    }

    public void setImagesSrc(String imagesSrc) {
        this.imagesSrc = imagesSrc;
    }

    public String getVideoSrc() {
        return videoSrc;
    }

    public void setVideoSrc(String videoSrc) {
        this.videoSrc = videoSrc;
    }

    public String getAudioSrc() {
        return audioSrc;
    }

    public void setAudioSrc(String audioSrc) {
        this.audioSrc = audioSrc;
    }
}
