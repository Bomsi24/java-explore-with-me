package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("начало работы метода createCategory");
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Имя должно быть уникальным", "");
        }

        Category newCategory = CategoryMapper.mapCategory(newCategoryDto);
        categoryRepository.save(newCategory);

        return CategoryMapper.mapCategoryDto(newCategory);
    }

    @Override
    public void deleteCategory(int catId) {
        log.info("начало работы метода deleteCategory");
        categoryRepository.findById(catId).orElseThrow(() ->
                new ConflictException("Category with id=" + catId + " was not found",
                        "The required object was not found."));

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("Категория связанна с ивентом", "");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(int catId, CategoryDto categoryDto) {
        log.info("начало работы метода updateCategory");
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Такой категории нет", ""));

        if(categoryRepository.existsByName(categoryDto.getName())
                && !categoryDto.getName().equals(category.getName())) {
            throw new ConflictException("Имя должно быть уникальным", "");
        }

        category.setName(categoryDto.getName());
        Category newCategory = categoryRepository.save(category);

        return CategoryMapper.mapCategoryDto(newCategory);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        log.info("начало работы метода getCategories");
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.getContent().stream()
                .map(CategoryMapper::mapCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(int id) {
        log.info("начало работы метода getCategory");
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категории нет", ""));

        return CategoryMapper.mapCategoryDto(category);
    }
}
