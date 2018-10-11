package org.soul;

import javafx.application.Application;
import javafx.stage.Stage;
import org.soul.controller.GameApplicationController;
import org.soul.model.GameApplicationModel;

public class Client extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameApplicationModel applicationModel = new GameApplicationModel();
        GameApplicationController applicationController = new GameApplicationController(applicationModel);
        primaryStage.setTitle(applicationModel.getTitle());
        primaryStage.setScene(applicationModel.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
