package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            return null;
        }
        Category newCategory = CategoryMapper.mapCategory(newCategoryDto);
        categoryRepository.save(newCategory);
        return CategoryMapper.mapCategoryDto(newCategory);
    }

    @Override
    public void deleteCategory(Integer catId) {
        //Обратите внимание: с категорией не должно быть связано ни одного события
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        if (categoryRepository.existsById(catId)) {
            return null;//Вернуть ошибку. Имя не уникально
        }
        Category category = categoryRepository.findById(catId).orElse(null);
        if (category == null) {
            return null;// Вернуть ошибку такой категории нет
        }
        category.setName(category.getName());
        Category newCategory = categoryRepository.save(category);

        return CategoryMapper.mapCategoryDto(newCategory);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.getContent().stream()
                .map(CategoryMapper::mapCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow();// должно вернуть ошибку 404

        return CategoryMapper.mapCategoryDto(category);
    }
}
