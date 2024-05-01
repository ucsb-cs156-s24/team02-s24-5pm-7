package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.validation.Valid;

@Tag(name = "UCSBArticles")
@RequestMapping("/api/articles")
@RestController
@Slf4j
public class ArticlesController extends ApiController {

    @Autowired
    ArticlesRepository articlesRepository; 

    @Operation(summary= "List all articles")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<Articles> allArticless() {
        Iterable<Articles> articles = articlesRepository.findAll();
        return articles;
    }

    @Operation(summary= "Create a new article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public Articles postArticles(
        @Parameter(name="title") @RequestParam String title,
        @Parameter(name="url") @RequestParam String url,
        @Parameter(name="explanation") @RequestParam String explanation,
        @Parameter(name="email") @RequestParam String email,
        @Parameter(name="dateAdded") @RequestParam("dateAdded") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateAdded)
        
        throws JsonProcessingException {
            Articles articles = new Articles();
            articles.setTitle(title);
            articles.setUrl(url);
            articles.setExplanation(explanation);
            articles.setEmail(email);
            articles.setDateAdded(dateAdded);

            Articles savedArticles = articlesRepository.save(articles);

            return savedArticles;}
            //Get
            @Operation(summary= "Get a single article")
            @PreAuthorize("hasRole('ROLE_USER')")
            @GetMapping("")
            public Articles getById(
                    @Parameter(name="id") @RequestParam Long id) {
                Articles article = articlesRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(Articles.class, id));
        
                return article;
            }
    //delete
    @Operation(summary= "Delete an Article")    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteArticle(
        @Parameter(name="id") @RequestParam Long id) {
            Articles articles = articlesRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException(Articles.class, id));
    
            articlesRepository.delete(articles);
            return genericMessage("Articles with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update a single article")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public Articles updateArticles(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid Articles incoming) {

            Articles articles = articlesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Articles.class, id));

            articles.setTitle(incoming.getTitle());
            articles.setUrl(incoming.getUrl());
            articles.setExplanation(incoming.getExplanation());
            articles.setEmail(incoming.getEmail());
            articles.setDateAdded(incoming.getDateAdded());

        articlesRepository.save(articles);

        return articles;
    }

}