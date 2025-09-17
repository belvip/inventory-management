package com.belvinard.inventory_management.controller;

import com.belvinard.inventory_management.config.AppConstant;
import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/articles")
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


    @Operation(
            summary = "Archive an article",
            description = "Sets the article status to ARCHIVED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article archived successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/archive")
    public ResponseEntity<ArticleResponseDto> archiveArticle(@PathVariable Long id) {
        ArticleResponseDto archivedArticle = articleService.archiveArticle(id);
        return new ResponseEntity<>(archivedArticle, HttpStatus.OK);
    }

    @Operation(
            summary = "Restore an article",
            description = "Restores an archived article to ACTIVE status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article restored successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<ArticleResponseDto> restoreArticle(@PathVariable Long id) {
        ArticleResponseDto restoredArticle = articleService.restoreArticle(id);
        return new ResponseEntity<>(restoredArticle, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all archived articles - ADMIN MANAGER or SALES",
            description = "Retrieve a list of all articles with status ARCHIVED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archived articles retrieved successfully")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_SALES')")
    @GetMapping("/archived")
    public ResponseEntity<List<ArticleResponseDto>> getAllArchivedArticles() {
        List<ArticleResponseDto> archivedArticles = articleService.getAllArchivedArticles();
        return new ResponseEntity<>(archivedArticles, HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing article - Only ADMIN and MANAGER",
            description = "Updates an article's details. " +
                    "Validates uniqueness of article code and defaults quantity to 0 if not provided."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article updated successfully",
                    content = @Content(schema = @Schema(implementation = ArticleResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failed",
                    content = @Content(schema = @Schema(example = """
                        {
                          "timestamp": "2025-09-17T10:15:30",
                          "status": 400,
                          "error": "Validation Error",
                          "errors": {
                            "codeArticle": "Article code must be unique"
                          }
                        }
                        """))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(example = """
                        {
                          "timestamp": "2025-09-17T10:15:30",
                          "status": 404,
                          "error": "Resource Not Found",
                          "message": "Article not found with id: 1"
                        }
                        """))),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ArticleResponseDto> updateArticle(
            @Parameter(description = "ID of the article to be updated", example = "1")
            @PathVariable Long id,

            @Valid @RequestBody ArticleRequestDto dto
    ) {
        ArticleResponseDto updatedArticle = articleService.updateArticle(id, dto);
        return ResponseEntity.ok(updatedArticle);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<ArticleResponseDto>> getAllArticles(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort field", example = "designation")
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_ARTICLES_BY, required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder
    ) {
        PagedResponse<ArticleResponseDto> pagedResponse = articleService.getAllArticle(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(pagedResponse);
    }

}
