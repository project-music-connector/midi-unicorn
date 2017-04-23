package ui;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;
import sound.algorithms.Keys;
import sound.algorithms.UnicornParser;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    UnicornParser parser;


    FileChooser fileIO = new FileChooser();
    int length = 20;
    int width = 20;

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
        public Paint getPaint() {
            return currentColor;
        }
    }



    public void start(Stage primaryStage) {
        parser = new UnicornParser();

        openFiles = new HashMap<>();

        mainView = new BorderPane();


        activePane = new GridPane();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                activePane.add(new ImageUnit(Paint.valueOf("white")),i,j);
            }
        }

        buttonBox = new HBox(20);

        playButton = new Button();
        playButton.setText("Play");
        playButton.setOnAction(e -> {
            // Turn GridPane into image.
            ObservableList<Node> children = activePane.getChildren();
            BufferedImage imageWriter = new BufferedImage(length,width,BufferedImage.TYPE_BYTE_GRAY);
            for (Node n : children) {
                imageWriter.setRGB(GridPane.getRowIndex(n),GridPane.getColumnIndex(n),
                        ((ImageUnit)n).getPaint().toString().equals("black") ? 0 : 255);
            }
            // parser.parse(imageWriter); // TODO: turn into int[][]
            // parser.setKey(key);
            // parser.play();
        });
        playButton.setPrefSize(100,100);
        buttonBox.getChildren().add(playButton);

        fileOpenButton = new Button();
        fileOpenButton.setText("Open File");
        fileOpenButton.setPrefSize(100,100);
        fileOpenButton.setOnAction(e -> {
            File imageFile = fileIO.showSaveDialog(primaryStage);
            BufferedImage userImage =
                    new BufferedImage(100,100,
                            BufferedImage.TYPE_INT_RGB);
            try {
                userImage = ImageIO.read(imageFile);
            } catch (IOException error) {
                error.printStackTrace();
            } finally {
                Color tmp;
                int[][] imageArray = new int[userImage.getHeight()][userImage.getWidth()];
                for (int i = 0; i < userImage.getHeight(); i++) {
                    for (int j = 0; j < userImage.getWidth(); j++) {
                        tmp = new Color(userImage.getRGB(i,j));
                        imageArray[i][j] =
                                (tmp.getRed()+tmp.getBlue()+tmp.getGreen())/3;
                    }
                }
                parser.parse(imageArray, Keys.A_FLAT_MJ);
            }
        });

        mainView.setCenter(activePane);
        mainView.setTop(buttonBox);
        Scene primaryScene = new Scene(mainView);

        primaryStage.setScene(primaryScene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
