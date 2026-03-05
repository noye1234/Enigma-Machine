package patmal.course.enigma.engine.MachineManager;
import org.springframework.stereotype.Component;
import patmal.course.enigma.engine.EngineImpl;
import patmal.course.enigma.engine.MachineModel.MachineData;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import patmal.course.enigma.engine.component.EnigmaRotor;
import patmal.course.enigma.engine.component.EnigmaReflector;
@Component
public class MachineManager {

    private final Map<String, EngineImpl> engines = new HashMap<>();
    public EngineImpl getMachineCopy(String name) {
        return engines.get(name).createCopy();
    }
    public MachineManager clone() {
        MachineManager clonedManager = new MachineManager();
        for (Map.Entry<String, EngineImpl> entry : engines.entrySet()) {
            clonedManager.addEngine(entry.getKey(), entry.getValue().createCopy());
        }
        return clonedManager;
    }

    public void addEngine(String name, EngineImpl engine) {
        engines.put(name, engine);
    }

    public boolean is_engine_exist(String name) {
        return engines.containsKey(name);
    }

    public EngineImpl getEngine(String name) {
        return engines.get(name);
    }

    public List<String> loadXml(InputStream inputStream) {
        EngineImpl tempEngine = new EngineImpl();

        List<String> errors = tempEngine.loadXml(inputStream);

        if (!errors.isEmpty()) {
            return errors;
        }

        String machineName = tempEngine.getMachineName();


        if (engines.containsKey(machineName)) {
            errors.add("Error: Engine with name '" + machineName + "' already exists in your session.");
            return errors;
        }

        addEngine(machineName, tempEngine);

        List<String> success = new ArrayList<>();
        success.add(machineName);

        return  success;
    }

    public List<MachineData> getMachineData(List<String> names) {
        List<MachineData> machineDataList = new ArrayList<>();

        if (names == null || names.isEmpty()) {
            names = new ArrayList<>(engines.keySet());
        }

        for (String name : names) {
            if (engines.containsKey(name)) {
                machineDataList.add(getEngine(name).getMachineData());
            }
        }
        return machineDataList;
    }

    public List<EnigmaRotor> getMachineRotors(String name) {
        if (!engines.containsKey(name)) {
            return List.of();
        }
        return engines.get(name).getAvailableRotors();
    }
    public List<EnigmaReflector> getMachineReflectors(String name) {
        if (!engines.containsKey(name)) {
            return List.of();
        }
        return engines.get(name).getAvailableReflectors();
    }
    public List<Character> getMachineABC(String name) {
        if (!engines.containsKey(name)) {
            return List.of();
        }
        return engines.get(name).getABC();
    }
}