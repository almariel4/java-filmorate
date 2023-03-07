package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Date;

@Data
public class User {

private int id;
@Email
@NotBlank
private String email;
@NotBlank
private String login;
private String name;
@PastOrPresent
private LocalDate birthday;


    public User(String email, String login, String name, LocalDate birthday) {
    }

    public User() {};
}
