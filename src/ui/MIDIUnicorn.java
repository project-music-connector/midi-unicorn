package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Asimm on 4/7/2017.
 */



/*
Root Node

BorderPane( Left: ListPane, Center: GridPane, Top: HBox ( FileOpenButton, FileSaveButton, PlayButton, EditGrid} )


 */

public class MIDIUnicorn extends Application {
    BorderPane mainView;

    ListView<HBox> sideView;
    Map<String,ArrayList> openFiles;
    GridPane activePane;

    HBox buttonBox;
    Button fileOpenButton;
    Button fileSaveButton;
    Button playButton;
    Button editGridButton;
    List<String> musicalKey;


    FileChooser fileIO;

    private class ImageUnit extends Rectangle {
        private Paint currentColor;
        private int value;

        public ImageUnit() {
            super(60,30);
            value = 0;
            this.setOnMouseClicked(event -> {
                if (value == 1) {
                    setFill(Paint.valueOf("white"));
                    value = 0;
                } else {
                    setFill(Paint.valueOf("black"));
                    value = 1;
                }
            });
        }
        public ImageUnit(Paint color) {
            this();
            setFill(color);
            setStroke(Paint.valueOf("black"));
        }
    }

    public void start(Stage primaryStage) {
        openFiles = new HashMap<>();

        mainView = new BorderPane();


        activePane = new GridPane();
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                activePane.add(new ImageUnit(Paint.valueOf("white")),i,j);
            }
        }

        buttonBox = new HBox(5.0);

        mainView.setCenter(activePane);
        Scene primaryScene = new Scene(mainView);

        primaryStage.setScene(primaryScene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
