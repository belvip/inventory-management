package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.dto.request.ArticleRequestDto;
import com.belvinard.inventory_management.dto.response.ArticleResponseDto;
import com.belvinard.inventory_management.dto.response.PagedResponse;
import com.belvinard.inventory_management.exception.APIException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ArticleResponseDto updateArticle(Long id, ArticleRequestDto dto) {
        // 1️⃣ Check if the article exists
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));

        articleRepository.findByCodeArticle(dto.codeArticle())
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> {
                    throw new ResourceConflictException("Another article with the same code already exists");
                });

        existingArticle.setCodeArticle(dto.codeArticle());
        existingArticle.setDesignation(dto.designation());

        existingArticle.setQuantityInStock(dto.quantityInStock() != null ? dto.quantityInStock() : 0L);

        existingArticle.setUnitPriceExclTax(dto.unitPriceExclTax());

        existingArticle.setRateTva(dto.rateTva() != null ? dto.rateTva() : BigDecimal.ZERO);

        existingArticle.setImage(dto.image());

        // 4️⃣ Recalculate the price with tax (Hibernate @PreUpdate will also do this before saving)
        existingArticle.calculateUnitPriceAllTax();

        Article updatedArticle = articleRepository.save(existingArticle);

        return articleMapper.toResponseDto(updatedArticle);
    }

    @Override
    public PagedResponse getAllArticle(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Article> articlePage = articleRepository.findAll(pageable);

        if(articlePage.isEmpty()){
            throw new APIException("No articles found");
        }

        List<ArticleResponseDto> content = articlePage
                .getContent()
                .stream()
                .map(articleMapper::toResponseDto)
                .toList();

        return new PagedResponse<>(
                content,
                articlePage.getNumber(),
                articlePage.getSize(),
                articlePage.getTotalElements(),
                articlePage.getTotalPages(),
                articlePage.isLast()
        );


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
