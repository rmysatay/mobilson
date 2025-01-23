package com.rmysatay.instagramclone.model;

public class Post {
    public String postId;
    public String email;
    public String comment;
    public String downloadUrl;

    public Post(String postId, String email, String comment, String downloadUrl) {
        this.postId = postId;
        this.email = email;
        this.comment = comment;
        this.downloadUrl = downloadUrl;
    }
}
