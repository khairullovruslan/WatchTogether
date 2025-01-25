module ru.itis.khairullovruslan.watchtogether {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens ru.itis.khairullovruslan.watchtogether to javafx.fxml;
    opens ru.itis.khairullovruslan.watchtogether.controllers to javafx.fxml;
    opens ru.itis.khairullovruslan.watchtogether.controllers.base to javafx.fxml;
    opens ru.itis.khairullovruslan.watchtogether.controllers.view to javafx.fxml;

    exports ru.itis.khairullovruslan.watchtogether;
    opens ru.itis.khairullovruslan.watchtogether.controllers.controller to javafx.fxml;
    opens ru.itis.khairullovruslan.watchtogether.controllers.controller.video to javafx.fxml;


}

