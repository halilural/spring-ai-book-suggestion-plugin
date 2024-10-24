package com.uralhalil.bookrecommenderapi.model;

import java.util.List;

public class BookResponse {

    private final List<Book> books;

    public BookResponse(List<Book> books) {
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }
}
