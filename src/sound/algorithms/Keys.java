package sound.algorithms;

/**
 * Created by Beatriz on 14/04/2017.
 */
public enum Keys {
    C_MJ(0, 0), C_SHARP_MJ(1, 0), D_MJ(3, 0), D_SHARP_MJ(4, 0), E_MJ(5, 0),
    F_MJ(6, 0), F_SHARP_MJ(7, 0), G_MJ(8, 0), G_SHARP_MJ(9, 0), A_MJ(10, 0),
    A_SHARP_MJ(11, 0), B_MJ(12, 0), B_FLAT_MJ(11, 0), A_FLAT_MJ(9, 0),
    G_FLAT_MJ(7, 0), E_FLAT_MJ(4, 0), D_FLAT_MJ(1, 0),
    C_MN(0, 1), C_SHARP_MN(1, 1), D_MN(3, 1), D_SHARP_MN(4, 1), E_MN(5, 1),
    F_MN(6, 1), F_SHARP_MN(7, 1), G_MN(8, 1), G_SHARP_MN(9, 1), A_MN(10, 1),
    A_SHARP_MN(11, 1), B_MN(12, 1), B_FLAT_MN(11, 1), A_FLAT_MN(9, 1),
    G_FLAT_MN(7, 1), E_FLAT_MN(4, 1), D_FLAT_MN(1, 1);

    private int value;
    private int quality;
    private Chord[] chords;

    Keys(int value, int quality) {
        this.value = value;
        this.quality = quality;
        chords = new Chord[7];
    }

    public int getValue() {
        return value;
    }

    public Chord[] getRelationTable() {
        if (quality == 0){
            chords[0] = Chord.MAJOR;
            chords[1] = Chord.MINOR;
            chords[2] = Chord.MINOR;
            chords[3] = Chord.MAJOR;
            chords[4] = Chord.MAJOR;
            chords[5] = Chord.MINOR;
            chords[6] = Chord.DIMINISHED;
        } else {
            chords[0] = Chord.MINOR;
            chords[1] = Chord.DIMINISHED;
            chords[2] = Chord.MAJOR;
            chords[3] = Chord.MINOR;
            chords[4] = Chord.MAJOR;
            chords[5] = Chord.MAJOR;
            chords[6] = Chord.MAJOR;
        }
        return chords;
    }
}