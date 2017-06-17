package sound.algorithms;
import java.util.*;

/**
 * Created by Alice on 6/16/2017.
 */

/**
 * HybridAlgorithm takes an image in int array of int arrays as input, and outputs an arrayList of arrayLists with the MIDI
 * notes to be played each column.
 *
 * The bottom note is always C, the top note is always C. This way, we ensure an integer number of octaves (+1 note)
 * I chord = 1, ii chord = 3, etc. We're sticking to C major right now.
 * C = 1, D = 2, etc.
 * Since we're using this MatLab-esque notation, be sure to always substract 1 when necessary
 */

public class HybridAlgorithm {
    private int[][] image; //array of columns. All pixels are represented -- value 1 if the pixel is dark, value 0 if the pixel is white.
    private int bottomOctave; //octave number of the lowest note
    private int chordCounter = 0; //number of times a chord has been repeated successively (not including first instance)
    private int prevChord;

    public HybridAlgorithm(int[][] image, int bottomOctave) {
        this.image = image;
        this.bottomOctave = bottomOctave;
    }

    //diatonic major scale:       I          ii        iii        IV          V          vi        vii
    private int[][] chords = {{1, 3, 5}, {2, 4, 6}, {3, 5, 7}, {4, 6, 1}, {5, 7, 2}, {6, 1, 3}, {7, 2, 4}};
    //ordered in priority: chords that contain the note. The priority only matters for the first column.
    //                                     1          2          3          4          5          6          7
    private int[][] chordsWithNotes = {{1, 3, 5}, {5, 7, 2}, {1, 6, 3}, {4, 7, 2}, {5, 1, 3}, {6, 4, 2}, {5, 7, 3}};
    //chords another chord can progress to, in order of priority
    //from:                             I          ii             iii           IV          V            vi            vii
    private int[][] progressions = {{5, 4, 6}, {5, 7, 1, 6}, {6, 4, 2, 5}, {5, 1, 2, 7}, {1, 6, 7}, {4, 2, 5, 1}, {5, 1, 6, 2}};

    //calculate the raw MIDI values of the dark pixels in a column
    private ArrayList<Integer> getRawNotes(int[] column) {
        ArrayList<Integer> rawNotes = new ArrayList<Integer>();
        //go through pixels from top to bottom
        for (int i = column.length - 1; i < column.length; i--) {
            if (column[i] == 1) {
                rawNotes.add((bottomOctave + 1)*12 + i); //C1 = 24; C2 = 36; Cn = 12(n+1)
            }
        }
        return rawNotes;
    }

    //get note name (C, D, E, etc.) given a MIDI value
    private int getNoteName(int num) {
        return (num % 12) + 1;
    }

    //tests whether an int array contains an int
    private boolean contain(int[] array, int val) {
        boolean bool = false;
        for (int i = 0; i < array.length; i++) {
            if (val == array[i]) {
                bool = true;
                break;
            }
        }
        return bool;
    }

    //given a chord and a column of raw inputs, returns list of notes that will be played
    private ArrayList<Integer> columnNoteList(ArrayList<Integer> rawNotes, int columnChord) {
        ArrayList<Integer> columnNotes = new ArrayList<Integer>();
        for (int i = 0; i < rawNotes.size(); i++) {
            if (contain(chords[columnChord - 1], rawNotes.get(i))) {
                columnNotes.add(rawNotes.get(i));
            }
        }
        return columnNotes;
    }

    //calculate the notes that will be played by the first column
    private ArrayList<Integer> firstColumnNotes(ArrayList<Integer> rawNotes) {
        int columnChord = 1; //chord that will be played this column, initialized to 1 for safety
        //if the top note is part of the I chord, play the I chord
        //otherwise, take the first-priority chord containing the top note
        int top = getNoteName(rawNotes.get(0));
        if (top == 1 || top == 3 || top == 5) {
                columnChord = 1;
        } else {
            columnChord = chordsWithNotes[top - 1][0];
        }
        prevChord = columnChord;

        return columnNoteList(rawNotes, columnChord);
    }

    //calculate the notes that will be played all subsequent columns (except the last one
    private ArrayList<Integer> nextColumnNotes(ArrayList<Integer> rawNotes) {
        int columnChord = 1; //chord that will be played this column, init to 1 for safety
        int top = getNoteName(rawNotes.get(0));
        //If TOP is element of PREVCHORD, play PREVCHORD, or if PREVCHORD has been played less than twice successively,
        //then play PREVCHORD.
        //Else, choose chord COLUMNCHORD such that TOP is element of COLUMNCHORD, and PREVCHORD -> COLUMNCHORD is a
        //valid transition (see array PROGRESSIONS)
        if (contain(chords[prevChord - 1], top) && chordCounter < 2) {
            columnChord = prevChord;
            chordCounter++;
        } else {
            int[] possibleChords = progressions[prevChord - 1]; //possible chord, considering progression
            for (int i = 0; i < possibleChords.length; i++) {   //test which chord contains top note
                if (contain(chords[possibleChords[i] - 1], top)) {
                    columnChord = possibleChords[i];
                    chordCounter = 0;
                    break;
                }
            }
        }
        prevChord = columnChord;
        return columnNoteList(rawNotes, columnChord);
    }

    //calculate the notes that will be played on the last column
    private ArrayList<Integer> lastColumnNotes(ArrayList<Integer> rawNotes) {
        int columnChord = 1; //play I chord for last column
        int top = getNoteName(rawNotes.get(0));
        ArrayList<Integer> columnNotes = new ArrayList<Integer>(); //list of notes that will be played this column
        columnNotes.addAll(columnNoteList(rawNotes, columnChord)); //add all notes part of the C major chord
        columnNotes.add(top); //add top note
        return columnNotes;
    }

    //give an arrayList of arrayLists representing the notes that should be played, every beat (array of columns)
    public ArrayList<ArrayList<Integer>> imageNotes() {
        ArrayList<ArrayList<Integer>> notes = new ArrayList<>();
        ArrayList<Integer> rest = new ArrayList<>();
        rest.add(0); //there's gotta be a better way to do this
        //if a row is empty, add an empty arraylist.
        //process first row
        if (getRawNotes(image[0]).size() > 0) {
            notes.add(firstColumnNotes(getRawNotes(image[0])));
        } else {
            notes.add(rest);
        }
        //process all subsequent rows
        for (int i = 1; i < image.length - 1; i++) {
            if (getRawNotes(image[i]).size() > 0) {
                notes.add(nextColumnNotes(getRawNotes(image[i])));
            } else {
                notes.add(rest);
            }
        }
        //process last row
        if (getRawNotes(image[image.length - 1]).size() > 0) {
            notes.add(lastColumnNotes(getRawNotes(image[image.length - 1])));
        } else {
            notes.add(rest);
        }
        return notes;
    }
}
