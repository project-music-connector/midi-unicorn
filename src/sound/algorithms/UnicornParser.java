package sound.algorithms;

import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import sound.gen.MidiNote;
import sound.gen.MidiPlayer;

import javax.sound.midi.MidiUnavailableException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.floor;

/**
 * Created by Asimm on 4/7/2017.
 */

public class UnicornParser {
    private int[][] image;

    public UnicornParser(WritableImage im, int octaves, int divisions) {
        int height = (int) floor(im.getHeight() / (octaves * 7));
        int width = (int) floor(im.getWidth() / divisions);
        image = new int[octaves * 7][divisions];
        PixelReader pr = im.getPixelReader();
        for (int x = 0; x < octaves * 7; x++) {
            for (int y = 0; y < divisions; y++) {
                int red = 0;
                int blue = 0;
                int green = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int clr = pr.getArgb(j+y*width, i+x*height);
                        red += (clr >> 16) & 0xff;
                        green += (clr >> 8) & 0xff;
                        blue += clr & 0xff;
                    }
                }
                if ((red/(height*width) + green/(height*width) + blue/(height*width))/3 < 175) {
                    image[x][y] = 1;
                }
            }
        }
    }

    public int[][] getImage() {
        return image;
    }
    public void setImage(int[][] image) {
        this.image = image;
    }
    public void play(int bottomOctave, int key, int tempo) {
        HybridAlgorithm alg = new HybridAlgorithm(image, bottomOctave, key, tempo);
        alg.imageNotes();
    }

}

