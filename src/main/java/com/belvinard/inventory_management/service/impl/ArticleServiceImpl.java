package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.exception.ResourceConflictException;
import com.belvinard.inventory_management.exception.ResourceNotFoundException;
import com.belvinard.inventory_management.mapper.ArticleMapper;
import com.belvinard.inventory_management.model.Article;
import com.belvinard.inventory_management.model.ArticleStatus;
import com.belvinard.inventory_management.model.Category;
import com.belvinard.inventory_management.repository.ArticleRepository;
import com.belvinard.inventory_management.repository.CategoryRepository;
import com.belvinard.inventory_management.service.ArticleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleResponseDto createArticle(ArticleRequestDto dto) {

        if (articleRepository.findByCodeArticle(dto.codeArticle()).isPresent()) {
            throw new ResourceConflictException("Article with code " + dto.codeArticle() + " already exists");
        }

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.categoryId()));

        Article article = articleMapper.toEntity(dto);

        if (article.getQuantityInStock() == null) article.setQuantityInStock(0L);
        if (article.getRateTva() == null) article.setRateTva(BigDecimal.ZERO);

        article.setCategory(category);

        // 7️⃣ Persist the article — @PrePersist will calculate unitPriceAllTax automatically
        Article savedArticle = articleRepository.save(article);

        return articleMapper.toResponseDto(savedArticle);
    }

    @Override
    public ArticleResponseDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        return articleMapper.toResponseDto(article);
    }


    @Override
    @Transactional
    public ArticleResponseDto deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        articleRepository.delete(article);
        return articleMapper.toResponseDto(article);
    }

    @Override
    public ArticleResponseDto getArticleByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Article code must not be null or empty");
        }

        Article article = articleRepository.findByCodeArticle(code)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with code: " + code));

        return articleMapper.toResponseDto(article);
    }

    @Override
    public ArticleResponseDto archiveArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        article.archive();
        Article updated = articleRepository.save(article);
        return articleMapper.toResponseDto(updated);
    }

    @Override
    public List<ArticleResponseDto> getAllArchivedArticles() {
        List<Article> archived = articleRepository.findByStatus(ArticleStatus.ARCHIVED);
        return archived.stream()
                .map(articleMapper::toResponseDto)
                .toList();
    }


    @Override
    public ArticleResponseDto restoreArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        article.restore();
        Article updated = articleRepository.save(article);
        return articleMapper.toResponseDto(updated);
    }



}
