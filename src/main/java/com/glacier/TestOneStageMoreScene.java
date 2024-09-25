package com.glacier;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 一个stage中实现多个scence切换
 * @since 2024/9/25 22:37
 */
public class TestOneStageMoreScene extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        showFirstScene();
    }

    private void showFirstScene() {
        Button btnSwitchToSecond = new Button("Go to Second Scene");
        btnSwitchToSecond.setOnAction(e -> showSecondScene());

        StackPane root = new StackPane();
        root.getChildren().add(btnSwitchToSecond);

        Scene scene = new Scene(root, 300, 250);
        stage.setTitle("Single Stage - First Scene");
        stage.setScene(scene);
        stage.show();
    }

    private void showSecondScene() {
        Button btnSwitchToFirst = new Button("Go back to First Scene");
        btnSwitchToFirst.setOnAction(e -> showFirstScene());

        StackPane root = new StackPane();
        root.getChildren().add(btnSwitchToFirst);

        Scene scene = new Scene(root, 300, 250);
        stage.setTitle("Single Stage - Second Scene");
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }


}
