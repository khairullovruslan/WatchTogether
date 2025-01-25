package ru.itis.khairullovruslan.watchtogether.exceptions.ui;

public class FontNotFoundException extends RuntimeException{
    public FontNotFoundException(String fontName) {
        super("font - %s not found".formatted(fontName));
    }

    public FontNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
