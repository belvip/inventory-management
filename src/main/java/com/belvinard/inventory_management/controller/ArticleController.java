package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
@Tag(name = "Articles", description = "Endpoints for managing articles")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
        summary = "Create a new article - Only ADMIN and MANAGER",
        description = "Creates an article and associates it with a category based on the categoryId provided in the request body."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Article created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<ArticleResponseDto> createArticle(
            @Valid @RequestBody ArticleRequestDto dto) {
        ArticleResponseDto createdArticle = articleService.createArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @Operation(summary = "Get an article by ID - ADMIN MANAGER or SALES")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article found",
                    content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseDto> getArticleById(@PathVariable Long id) {
        ArticleResponseDto article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @Operation(summary = "Get an article by code - ADMIN MANAGER or SALES")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article found",
                    content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid code supplied",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/code/{code}")
    public ResponseEntity<ArticleResponseDto> getArticleByCode(@PathVariable String code) {
        ArticleResponseDto article = articleService.getArticleByCode(code);
        return ResponseEntity.ok(article);
    }

    @Operation(summary = "Delete an article by ID Only ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article deleted",
                    content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleResponseDto> deleteArticle(@PathVariable Long id) {
        ArticleResponseDto deletedArticle = articleService.deleteArticle(id);
        return ResponseEntity.ok(deletedArticle);
    }
}
