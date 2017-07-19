package sound.gen;


import javax.sound.midi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by Asimm on 4/7/2017.
 */
public class MidiPlayer {
    private static final int VELOCITY = 64;
    private Sequencer sequencer;
    private Sequence sequence;
    private List<MidiNote> prev;
    private Track track;

    public MidiPlayer() throws MidiUnavailableException {
        sequence = null;
        try {
            sequence = new Sequence(Sequence.PPQ, 1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.out.println("init");
            System.exit(1);
        }
        sequencer = MidiSystem.getSequencer();
        if (sequencer != null) {
            sequencer.open();
        }
        prev = new ArrayList<>();
        track = sequence.createTrack();
        try {
            sequencer.setSequence(sequence);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.out.println("PlaySheet");
        }
        sequencer.recordEnable(track, 1);
    }

    public void process(List<MidiNote> notes, int tick) {
        for (MidiNote n : prev) {
            if (!notes.isEmpty() && !notes.contains(n)) {
                track.add(createNoteOffEvent(n.getKey(), tick));
            } else if (notes.isEmpty()){
                track.add(createNoteOffEvent(n.getKey(), tick));
            }
        }
        for (MidiNote n : notes) {
            if (!prev.isEmpty() && !prev.contains(n)) {
                track.add(createNoteOnEvent(n.getKey(), tick));
            } else if (prev.isEmpty()){
                track.add(createNoteOnEvent(n.getKey(), tick));
            }
        }
        prev = notes;
    }

    private static MidiEvent createNoteOnEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_ON, nKey, VELOCITY, lTick);
    }

    private static MidiEvent createNoteOffEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_OFF, nKey, 0, lTick);
    }

    private static MidiEvent createNoteEvent(int nCommand, int nKey, int nVelocity, long lTick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(nCommand, 0, nKey, nVelocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.out.println("event");
            System.exit(1);
        }
        return new MidiEvent(message, lTick);
    }

    public void playSheet(ArrayList<ArrayList<MidiNote>> sheet, int tempo) {
        for (int i = 0; i < sheet.size(); i++) {
            process(sheet.get(i), i);
        }
        sequencer.stopRecording();
        sequencer.setTempoInBPM(tempo);
        sequencer.start();
    }
}
