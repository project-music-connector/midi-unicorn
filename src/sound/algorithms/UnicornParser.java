package sound.algorithms;

import sound.gen.MidiNote;
import sound.gen.MidiPlayer;

import javax.sound.midi.MidiUnavailableException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Asimm on 4/7/2017.
 */

public class UnicornParser {
    private int[][] image;

    public int[][] parse(BufferedImage im, int octaves, int divisions) {
        int height = im.getHeight() / (octaves * 7);
        int width = im.getWidth() / divisions;
        image = new int[height][width];
        for (int x = 0; x < octaves * 7; x++) {
            for (int y = 0; y < divisions; y++) {
                int red = 0;
                int blue = 0;
                int green = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int clr = im.getRGB(i, j);
                        red += (clr & 0x00ff0000) >> 16;
                        green += (clr & 0x0000ff00) >> 8;
                        blue += clr & 0x000000ff;
                    }
                }
                if ((red + blue + green) / (3 * width * height) > 70) {
                    image[x][y] = 1;
                }
            }
        }
        return image;
    }

    public void play(int bottomOctave, int key, int tempo) {
        HybridAlgorithm alg = new HybridAlgorithm(image, bottomOctave, key, tempo);
        alg.imageNotes();
    }

}

