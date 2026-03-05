package patmal.course.enigma.ProcessRequestDTO;

import java.util.List;

public class ManualCodeRequest {
    public String sessionID;
    public List<RotorSelection> rotors;
    public String reflector;
    public List<PlugConnection> plugs;
}