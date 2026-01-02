package patmal.course.enigma.engine;

import patmal.course.enigma.engine.MachineModel.MachineData;
import patmal.course.enigma.engine.component.Code;

import java.util.List;

public interface Engine {
    List<String> loadXml(String path);
    MachineData getMachineData();
    Code codeAutomatic();
    List<String> process(String message);
    List<String>  statistics();
    String codeManual(String code);
    public void restartCode();
    public void exit();
}