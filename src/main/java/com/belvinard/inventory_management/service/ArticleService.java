package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;

import java.util.List;

public interface ArticleService {

    ArticleResponseDto createArticle(ArticleRequestDto dto);

    ArticleResponseDto getArticleById(Long id);
    ArticleResponseDto deleteArticle(Long id);
    ArticleResponseDto getArticleByCode(String code);
    ArticleResponseDto restoreArticle(Long id);
    ArticleResponseDto archiveArticle(Long id);

    List<ArticleResponseDto> getAllArchivedArticles();
    /*ArticleResponseDto updateArticle(Long id, ArticleRequestDto dto);
    ArticleResponseDto updateArticleQuantity(Long id, Long quantity);*/
}
