package com.uralhalil.bookrecommenderapi.controller;

import com.uralhalil.bookrecommenderapi.model.Book;
import com.uralhalil.bookrecommenderapi.model.BookResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final EmbeddingModel embeddingModel;
    private final JdbcClient jdbcClient;

    @Autowired
    public BookController(EmbeddingModel embeddingModel, JdbcClient jdbcClient) {
        this.embeddingModel = embeddingModel;
        this.jdbcClient = jdbcClient;
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Integer id) {
        Book book = jdbcClient.sql("SELECT * FROM book WHERE id = ?")
                .param(id)
                .query(Book.class)
                .single();

        return new BookResponse(List.of(book));
    }

    @GetMapping("/search")
    public BookResponse searchBooks(@RequestParam("prompt") String prompt) {

        float[] embedding = embeddingModel.embed(prompt);

        List<Book> books =
                jdbcClient.sql("SELECT id, title, author, rating, price, description " +
                                "FROM book " +
                                "ORDER BY description_vector <=> :prompt_vector::vector LIMIT 3")
                        .param("prompt_vector", embedding)
                        .query(Book.class)
                        .list();

        // Step 4: Return results as a response
        return new BookResponse(books);
    }

}
