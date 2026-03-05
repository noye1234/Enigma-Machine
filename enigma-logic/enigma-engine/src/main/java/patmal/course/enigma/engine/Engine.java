package patmal.course.enigma.engine;

import patmal.course.enigma.engine.MachineModel.MachineData;
import patmal.course.enigma.engine.component.Code;

import java.io.InputStream;
import java.util.List;

public interface Engine {
    List<String> loadXml(InputStream inputStream);
    MachineData getMachineData();
    Code codeAutomatic();
    List<String> process(String message);
    List<String>  statistics();
    String codeManual(String code);
    public String restartCode();
    public void exit();
}