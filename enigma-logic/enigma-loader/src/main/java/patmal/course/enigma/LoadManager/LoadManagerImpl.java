package patmal.course.enigma.LoadManager;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.Serializable;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream; // ספרייה חדשה
import java.io.Serializable;

public class LoadManagerImpl implements LoadManager, Serializable {

    public <T> T load(InputStream inputStream, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // unmarshal יכול לקרוא ישירות מ-InputStream
        @SuppressWarnings("unchecked")
        T loadedObject = (T) unmarshaller.unmarshal(inputStream);

        return loadedObject;
    }



}