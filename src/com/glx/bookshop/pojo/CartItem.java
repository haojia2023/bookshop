package com.glx.bookshop.pojo;

public class CartItem {
    private Integer id;
    private  Book book;
    private Integer butCount;
    private User user;
    public CartItem(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getButCount() {
        return butCount;
    }

    public void setButCount(Integer butCount) {
        this.butCount = butCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
