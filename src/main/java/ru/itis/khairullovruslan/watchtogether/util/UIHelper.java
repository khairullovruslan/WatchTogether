package ru.itis.khairullovruslan.watchtogether.util;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import ru.itis.khairullovruslan.watchtogether.WatchTogetherApplication;
import ru.itis.khairullovruslan.watchtogether.exceptions.ui.FontNotFoundException;

import java.io.InputStream;
import java.util.Objects;

import static ru.itis.khairullovruslan.watchtogether.constants.StyleConstants.FONT_PATH;

public class UIHelper {

    public static Font getFont(Integer size) {
        InputStream fontStream = UIHelper.class.getResourceAsStream(FONT_PATH);
        if (fontStream == null) {
            throw new FontNotFoundException(FONT_PATH);
        }
        return Font.loadFont(fontStream, size)
                ;
    }

    public static Image getImage(String imagePath) {
        return new Image(Objects.requireNonNull(WatchTogetherApplication.class.getResourceAsStream(imagePath)));
    }
}
