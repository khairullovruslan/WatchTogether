package ru.itis.khairullovruslan.watchtogether.util;

import java.util.List;
import java.util.Random;

public class RandomColorGenerator {

    //blue
    private final static List<String> colors = List.of("green", "yellow", "orange", "pink", "purple");

    public static String generate(){
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }
}
