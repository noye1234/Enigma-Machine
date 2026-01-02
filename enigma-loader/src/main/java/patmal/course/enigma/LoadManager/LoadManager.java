package patmal.course.enigma.LoadManager;

import jakarta.xml.bind.JAXBException;

public interface LoadManager {
    /**
     * Loads and unmarshalls an XML file into a Java object.
     */
    <T> T load(String path, Class<T> clazz) throws JAXBException;

   // void saveMachineState(String filePath, EnigmaMachineModel machineModel, ArrayList<Code> code);
}