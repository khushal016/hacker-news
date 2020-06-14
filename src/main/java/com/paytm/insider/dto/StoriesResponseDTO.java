package com.paytm.insider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoriesResponseDTO {

    private List<Stories> data;
    private String message;
    private boolean success = true;

    public static class Stories {
        private String title;
        private String url;
        private Integer score;
        private String time;
        private String user;

        public Stories(String title, String url, Integer score, String time, String user) {
            this.title = title;
            this.url = url;
            this.score = score;
            this.time = time;
            this.user = user;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }

    public StoriesResponseDTO(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    public StoriesResponseDTO(List<Stories> data) {
        this.data = data;
    }

    public List<Stories> getData() {
        return data;
    }

    public void setData(List<Stories> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
