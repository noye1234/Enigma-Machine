package patmal.course.enigma.ProcessRequestDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigStatusResponse {
    public int totalRotors;
    public int totalReflectors;
    public int totalProcessedMessages;

    public EnigmaCodeStructure originalCode;
    public EnigmaCodeStructure currentRotorsPosition;

    public String originalCodeCompact;
    public String currentRotorsPositionCompact;
}