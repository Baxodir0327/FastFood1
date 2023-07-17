package org.example.server.service;


import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.example.server.model.Category;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class CategoryService implements BaseService<Category> {
    static CategoryService categoryService = new CategoryService();
    Path path = Path.of("src/main/resources/category.json");

    @Override
    public Category add(Category category) {
        List<Category> categories = readFile();
        categories.add(category);
        writeFile(categories);
        return category;
    }

    @Override
    public List<Category> getAll() {
        return readFile();
    }

    @Override
    public Category getById(UUID id) {
        return readFile().stream()
                .parallel()
                .filter(category -> category.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(UUID id) {
        List<Category> categories = readFile();
        categories.removeIf(category -> category.getId().equals(id));
        writeFile(categories);
    }

    @Override
    public Category update(Category category) {
        delete(category.getId());
        add(category);
        return category;
    }

    @SneakyThrows
    @Override
    public void writeFile(List<Category> list) {
        Files.writeString(path, gson.toJson(list), StandardOpenOption.TRUNCATE_EXISTING);
    }

    @SneakyThrows
    @Override
    public List<Category> readFile() {
        return gson.fromJson(Files.readString(path), new TypeToken<List<Category>>() {
        }.getType());
    }

    public List<String> getMainPageCategory(boolean admin) {
        List<Category> mainButtons = categoryService.getAll();
        List<String> buttons = mainButtons.stream().filter(category -> Objects.equals(category.getParentName(), "MainPage")).map(category -> category.getName()).collect(Collectors.toList());

        if (admin) {
            List<String> adminButtons = mainButtons.stream().filter(category -> Objects.equals(category.getParentName(), "adminMainPage")).map(Category::getName).toList();
            buttons.addAll(adminButtons);
        }
        return buttons;
    }

    public void deleteByName(String text) {
        Category category1 = getAll().stream().filter(category -> category.getName().equals(text)).findAny().get();
        delete(category1.getId());
    }
}
