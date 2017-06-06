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

    public void play(Keys key) {
        for (int i = 0; i < image[0].length; i++) {
            int j = image.length - 1;
            while (j >= 0 && image[j][i] != 1) {
                j--;
            }
            int base = j % 7;
            while (j >= 0) {
                if (image[j][i] == 1) {
                    image[j][i] = getNote(key, base, j % 7) + 1;
                }
                j--;
            }
        }
        ArrayList<ArrayList<MidiNote>> playable = new ArrayList<>(image[0].length);
        for (int i = 0; i < image[0].length; i++) {
            playable.add(new ArrayList<>());
        }
        for (int i = 0; i < image.length; i++) {
            int previous = 0;
            int initial = -1;
            int count = 0;
            for (int j = 0; j <= image[0].length; j++) {
                if (j == image[0].length || (image[j][i] != 0 && image[j][i] != previous)) {
                    if (initial != -1) {
                        int note = (image[initial][i] + key.getValue()) + i*12/7 - image.length/7/2*12 + 63;
                        playable.get(i).add(new MidiNote(note, 1,count));
                    }
                    initial = j;
                    count = 1;
                } else if (image[j][i] != 0 && image[j][i] == previous) {
                    count++;
                }
                previous = image[j][i];
            }
        }
        try {
            MidiPlayer midi = new MidiPlayer();
            midi.playSheet(playable);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private int getNote(Keys key, int base, int note) {
        int[] val = key.getRelationTable()[base].getProgression();
        switch (note) {
            case 0: return val[0];
            case 2: return val[1];
            case 4: return val[2];
            default: return -2;
        }
    }

}

