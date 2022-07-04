package com.spring_web_book.springbookservice.pojos;

public class LikeRequest {

    private boolean liked;

    public LikeRequest() {
    }

    public LikeRequest(boolean liked) {
        this.liked = liked;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
