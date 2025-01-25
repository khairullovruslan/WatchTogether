package ru.itis.khairullovruslan.watchtogether.controllers.controller;

import ru.itis.khairullovruslan.watchtogether.constants.FxmlConstants;
import ru.itis.khairullovruslan.watchtogether.controllers.view.PreWatchView;
import ru.itis.khairullovruslan.watchtogether.util.ScreenVisualizer;

public class PreWatchController {

    private final PreWatchView preWatchViewController;

    public PreWatchController(PreWatchView preWatchViewController) {
        this.preWatchViewController = preWatchViewController;
    }


    public void setMouseClickedEvents() {
        preWatchViewController.getCreateRoomLabel().setOnMouseClicked(
                mouseEvent -> ScreenVisualizer.show(FxmlConstants.CREATE_ROOM_SCREEN));
        preWatchViewController.getAddedByCodeLabel().setOnMouseClicked(
                mouseEvent -> ScreenVisualizer.show(FxmlConstants.JOIN_THE_ROOM_SCREEN));

    }


}
