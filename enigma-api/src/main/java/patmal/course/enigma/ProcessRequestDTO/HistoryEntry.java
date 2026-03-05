package patmal.course.enigma.ProcessRequestDTO;

public class HistoryEntry {
    public String input;
    public String output;
    public long duration;

    public HistoryEntry(String input, String output, long duration) {
        this.input = input;
        this.output = output;
        this.duration = duration;
    }
}