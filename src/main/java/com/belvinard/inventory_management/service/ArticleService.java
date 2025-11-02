package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {

    ArticleResponseDto createArticle(ArticleRequestDto dto);

    ArticleResponseDto getArticleById(Long id);
    ArticleResponseDto deleteArticle(Long id);
    ArticleResponseDto getArticleByCode(String code);
    ArticleResponseDto restoreArticle(Long id);
    ArticleResponseDto archiveArticle(Long id);

    List<ArticleResponseDto> getAllArchivedArticles();
    ArticleResponseDto updateArticle(Long id, ArticleRequestDto dto);
    PagedResponse getAllArticle(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ArticleResponseDto updateArticleImage(Long id, MultipartFile image) throws java.io.IOException;
    String getArticleImageUrl(Long id, Integer expirationMinutes);
    Long countLowStockArticles();

}
