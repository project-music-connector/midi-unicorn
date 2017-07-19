package sound.gen;

/**
 * Created by Asimm on 4/7/2017.
 */
public class MidiNote {
    private int key;
    private int velocity;
    private int duration;

    public static final int baseDuration = 200;

    public MidiNote(int key, int velocity, int tempo) {
        this.key = key;
        this.velocity = velocity;
        this.duration = tempo;
    }

    public int getKey() {
        return key;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getDuration() {
        return duration;
    }
}
