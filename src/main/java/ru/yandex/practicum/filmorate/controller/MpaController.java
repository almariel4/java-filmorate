package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@AllArgsConstructor
public class MpaController {

    private final FilmStorage filmStorage;

    @GetMapping
    public List<Mpa> getAllMpa() {
        return filmStorage.getAllMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") int id) {
        return filmStorage.getMpa(id);
    }

}
