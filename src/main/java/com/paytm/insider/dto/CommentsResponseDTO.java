package com.paytm.insider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentsResponseDTO {

    private boolean success = true;
    private String message;
    private List<Comment> data;

    public CommentsResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public CommentsResponseDTO(List<Comment> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }

    public static class Comment {
        private String text;
        private String user;
        @JsonProperty(value = "user_age")
        private int age;

        public Comment(String text, String user, int age) {
            this.text = text;
            this.user = user;
            this.age = age;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
