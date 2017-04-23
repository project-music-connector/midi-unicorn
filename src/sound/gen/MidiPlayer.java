package sound.gen;


import javax.sound.midi.Synthesizer;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by Asimm on 4/7/2017.
 */
public class MidiPlayer {
    private Synthesizer midiSynth;
    private Instrument[] instr;
    private MidiChannel[] mChannels;

    Stack<NotePlayer> activeNotes;
    Stack<Thread> activeNoteThreads;

    private class NotePlayer implements Runnable {
        private MidiNote note;

        public NotePlayer(MidiNote note) {
            this.note = note;
        }

        private synchronized  void noteOff() {
            mChannels[0].noteOff(note.getKey());
            activeNotes.remove(activeNotes.search(this));
        }

        private synchronized void noteOn() {
            mChannels[0].noteOn(note.getKey(),1);
        }

        public void run() {
            noteOn();
            try {
                Thread.sleep(note.getDuration());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            noteOff();
        }
    }

    public MidiPlayer() throws MidiUnavailableException {
        midiSynth = MidiSystem.getSynthesizer();
        instr = midiSynth.getDefaultSoundbank().getInstruments();
        mChannels = midiSynth.getChannels();
        midiSynth.loadInstrument(instr[0]);
        activeNotes = new Stack<NotePlayer>();
        activeNoteThreads = new Stack<Thread>();
    }

    public void play(MidiNote note) {
        activeNotes.push(new NotePlayer(note));
        activeNoteThreads.push(new Thread(activeNotes.peek()));
        activeNoteThreads.peek().start();
    }

    public void play(List<MidiNote> notes) {
        for (MidiNote n : notes) {
            play(n);
        }
    }

    public void playSheet(ArrayList<ArrayList<MidiNote>> sheet) {
        for (ArrayList<MidiNote> column : sheet) {
            play(column);
            try {
                wait(MidiNote.baseDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
