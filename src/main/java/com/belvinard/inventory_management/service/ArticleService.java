package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;

public interface ArticleService {

    ArticleResponseDto createArticle(ArticleRequestDto dto);
}
