package ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;
import sound.algorithms.UnicornParser;
import sound.gen.Key;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.round;

/**
 * Created by Asimm on 4/7/2017.
 */



/*
Root Node

BorderPane( Left: ListPane, Center: GridPane, Top: HBox ( FileOpenButton, FileSaveButton, PlayButton, EditGrid} )


 */

public class MIDIUnicorn extends Application {

    private Stage window;
    private Scene startScene;
    private Scene drawScene;
    private Scene playScene;
    private int w, h;
    private Slider octaves, bars, tempo;
    private ComboBox<Key> key;
    private BufferedImage userImage;
    private ImageView imView;
    private WritableImage wr;
    private double x, y;
    private File file;

    public static final int SCREEN_WIDTH = 512;
    public static final int TILE_WIDTH = 32;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        setupStartScene();
        window.setScene(startScene);
        window.setResizable(true);
        window.show();
    }

    private void setupStartScene() {
        Label label = new Label("MIDI Unicorn");
        label.setFont(new Font(36));
        Button fileChoice = new Button("Choose File");
        Button defaultChoice = new Button("Use White Canvas");
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Picture");
        file = null;
        fileChoice.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                file = fileChooser.showOpenDialog(window);
                if (file != null) {
                    setupDrawScene();
                    window.setScene(drawScene);
                }
            }
        });
        defaultChoice.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                setupDrawScene();
                window.setScene(drawScene);
            }
        });
        HBox hBox = new HBox(15);
        hBox.getChildren().addAll(fileChoice, defaultChoice);
        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(label, hBox);
        hBox.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMargin(label, new Insets(0, 0, 150, 0));
        StackPane pane = new StackPane(vBox);
        startScene = new Scene(pane, SCREEN_WIDTH, SCREEN_WIDTH);
    }


    private void setupDrawScene() {
        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                setupStartScene();
                window.setScene(startScene);
            }
        });
        userImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = userImage.createGraphics();
        graphics.setPaint( new Color ( 255, 255, 255 ) );
        graphics.fillRect( 0, 0, userImage.getWidth(), userImage.getHeight() );
        try {
            userImage = ImageIO.read(file);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
        h = (int) userImage.getHeight();
        w = (int) userImage.getWidth();
        wr = null;
        wr = new WritableImage(w, h);
        PixelWriter pw = wr.getPixelWriter();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pw.setArgb(i, j, userImage.getRGB(i, j));
            }
        }
        imView = new ImageView(wr);
        imView.setPreserveRatio(true);
        imView.setFitHeight(512);
        wr = imView.snapshot(null, null);
        h = (int) wr.getHeight();
        w = (int) wr.getWidth();
        octaves = new Slider(1, 8, 3);
        bars = new Slider(2, 50, 10);
        Slider paint = new Slider(1, 20, 10);
        octaves.setBlockIncrement(1);
        octaves.setMajorTickUnit(1);
        octaves.setMinorTickCount(0);
        octaves.setShowTickLabels(true);
        octaves.setShowTickMarks(true);
        octaves.setSnapToTicks(true);
        bars.setBlockIncrement(1);
        bars.setMajorTickUnit(4);
        bars.setMinorTickCount(3);
        bars.setShowTickLabels(true);
        bars.setShowTickMarks(true);
        bars.setSnapToTicks(true);
        paint.setBlockIncrement(1);
        paint.setMajorTickUnit(4);
        paint.setMinorTickCount(3);
        paint.setShowTickLabels(true);
        paint.setShowTickMarks(true);
        paint.setSnapToTicks(true);
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(w, h);
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(wr, 0, 0, w, h);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(5);
        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                w,    //width of the rectangle
                h);  //height of the rectangle
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        gc.setLineWidth(paint.getValue());
                        gc.beginPath();
                        gc.moveTo(event.getX(), event.getY());
                        gc.setStroke(javafx.scene.paint.Color.BLACK);
                        gc.stroke();
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {
                        gc.setLineWidth(paint.getValue());
                        gc.lineTo(event.getX(), event.getY());
                        gc.setStroke(javafx.scene.paint.Color.BLACK);
                        gc.stroke();
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>(){
                    @Override
                    public void handle(MouseEvent event) {

                    }
                });
        Button go = new Button("Go!");
        go.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                wr = canvas.snapshot(null, null);
                setupPlayScene(new UnicornParser(wr, (int)round(octaves.getValue()), (int)round(bars.getValue() * 4)));
                window.setScene(playScene);
            }
        });
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(back, 0, 0);
        grid.add(go, 0, 1);
        grid.add(new Label("Octaves: "), 1, 0);
        grid.add(new Label("Bars: "), 1, 1);
        grid.add(octaves, 2, 0);
        grid.add(bars, 2, 1);
        grid.add(new Label("Paintbrush size: "), 3, 0);
        grid.add(paint, 3, 1, 2, 1);
        grid.add(canvas, 0, 2,4, 3);
        drawScene = new Scene(grid);
    }

    private void setupPlayScene(UnicornParser parser) {
        Button back = new Button("Back");
        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                setupDrawScene();
                window.setScene(drawScene);
            }
        });
        octaves = new Slider(1, 8, octaves.getValue());
        bars = new Slider(2, 50, bars.getValue());
        tempo = new Slider(60, 240, 100);
        octaves.setBlockIncrement(1);
        octaves.setMajorTickUnit(1);
        octaves.setMinorTickCount(0);
        octaves.setShowTickLabels(true);
        octaves.setShowTickMarks(true);
        octaves.setSnapToTicks(true);
        bars.setBlockIncrement(1);
        bars.setMajorTickUnit(4);
        bars.setMinorTickCount(3);
        bars.setShowTickLabels(true);
        bars.setShowTickMarks(true);
        bars.setSnapToTicks(true);
        tempo.setBlockIncrement(5);
        tempo.setMajorTickUnit(30);
        tempo.setMinorTickCount(5);
        tempo.setShowTickLabels(true);
        tempo.setShowTickMarks(true);
        tempo.setSnapToTicks(true);
        octaves.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
            {
                setupPlayScene(new UnicornParser(wr, (int)round(octaves.getValue()), (int)round(bars.getValue() * 4)));
                window.setScene(playScene);
            }
        });
        bars.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue)
            {
                setupPlayScene(new UnicornParser(wr, (int)round(octaves.getValue()), (int)round(bars.getValue() * 4)));
                window.setScene(playScene);
            }
        });
        key = new ComboBox<>();
        key.getItems().addAll(
                Key.C,
                Key.D,
                Key.E,
                Key.F,
                Key.G,
                Key.A,
                Key.B
        );
        key.setValue(Key.C);
        int[][] im = parser.getImage();
        GridPane activePane = new GridPane();
        for (int i = 0; i < im.length; i++) {
            for (int j = 0; j < im[0].length; j++) {
                if (im[i][j] == 1) {
                    activePane.add(new ImageUnit(i, j, Paint.valueOf("black"), 1, 512/im.length, (int)(wr.getWidth()*512/wr.getHeight()/im[0].length)),j,i);
                } else {
                    activePane.add(new ImageUnit(i, j, 512/im.length, (int)(wr.getWidth()*512/wr.getHeight()/im[0].length)), j, i);
                }
            }
        }
        Button go = new Button("Play");
        go.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                for (Node node : activePane.getChildren()) {
                    if (node instanceof ImageUnit) {
                        im[((ImageUnit) node).getXVal()][((ImageUnit) node).getYVal()] = ((ImageUnit) node).getValue();
                    }
                }
                parser.setImage(im);
                parser.play((int)octaves.getValue()/2 + 1, key.getValue().getKey(), (int)tempo.getValue());
            }
        });

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(back, 0, 0);
        grid.add(go, 0, 1);
        grid.add(new Label("Octaves: "), 1, 0);
        grid.add(new Label("Bars: "), 1, 1);
        grid.add(octaves, 2, 0);
        grid.add(bars, 2, 1);
        grid.add(new Label("Tempo: "), 3, 0);
        grid.add(tempo, 4, 0);
        grid.add(new Label("Key: "), 3, 1);
        grid.add(key, 4, 1);
        grid.add(activePane, 0, 2, 5, 3);
        playScene = new Scene(grid);
    }

    private class ImageUnit extends Rectangle {
        private Paint currentColor;
        private int value;
        private int x, y;

        public ImageUnit(int x, int y, int h, int w) {
            this(x, y, Paint.valueOf("white"), 0, h, w);
        }

        public ImageUnit(int x, int y, Paint color, int value, int h, int w) {
            super(w, h);
            this.x = x;
            this.y = y;
            setFill(color);
            setStroke(Paint.valueOf("black"));
            this.value = value;
            this.setOnMouseClicked(event -> {
                if (this.value == 1) {
                    setFill(Paint.valueOf("white"));
                    this.value = 0;
                } else {
                    setFill(Paint.valueOf("black"));
                    this.value = 1;
                }
            });
        }
        public Paint getPaint() {
            return currentColor;
        }
        public int getXVal() {
            return x;
        }
        public int getYVal() {
            return y;
        }
        public int getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
