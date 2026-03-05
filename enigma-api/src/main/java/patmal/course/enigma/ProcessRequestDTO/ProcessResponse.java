package patmal.course.enigma.ProcessRequestDTO;

public class ProcessResponse {
    public String output;
    public String currentRotorsPositionCompact;

    public ProcessResponse(String output, String currentRotorsPositionCompact) {
        this.output = output;
        this.currentRotorsPositionCompact = currentRotorsPositionCompact;
    }
}