package ru.itis.khairullovruslan.watchtogether.util;

public class NickNameValidator {
    public static boolean isValidate(String name){
        return name != null && name.matches("^[a-zA-Zа-яА-ЯёЁ0-9]*$");

    }
}
