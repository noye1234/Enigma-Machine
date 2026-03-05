package patmal.course.enigma.LoadManager;

import jakarta.xml.bind.JAXBException;

import java.io.InputStream;

public interface LoadManager {
    /**
     * Loads and unmarshalls an XML file into a Java object.
     */
    <T> T load(InputStream inputStream, Class<T> clazz) throws JAXBException;

   // void saveMachineState(String filePath, EnigmaMachineModel machineModel, ArrayList<Code> code);
}