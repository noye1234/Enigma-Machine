package patmal.course.enigma.engine.MachineModel;

import patmal.course.enigma.loader.schema.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import patmal.course.enigma.engine.component.*;

public class EnigmaMachineModel implements Serializable {

    private final List<Character> alphabet;
    private final List<EnigmaRotor> availableRotors; // משתמש ב-EnigmaRotor
    private final List<EnigmaReflector> availableReflectors; // משתמש ב-EnigmaReflector
    private final int rotorCount;
    private boolean run=true;


    public void changeRunMode(boolean mode){
        this.run=mode;
    }

    public List<Character> getAlphabet() {
        return alphabet;
    }
    public boolean getRunMode(){
        return this.run;
    }
    private List<Character> createAlphabetSet(String ABC) {
        ArrayList<Character> list = new ArrayList();
        for (int i = 0; i < ABC.length(); i++) {
            list.add(ABC.charAt(i));
        }
        return list;
    }


    /** The Main Constructor/Mapper that converts the JAXB object structure into the clean Model. */
    public EnigmaMachineModel(BTEEnigma configData) {

       this.alphabet= createAlphabetSet(configData.getABC().trim());

        this.rotorCount = configData.getRotorsCount().intValue();

        this.availableRotors = configData.getBTERotors().getBTERotor().stream()
                .map(EnigmaRotor::new) // קונסטרוקטור המרה

                .collect(Collectors.toList());

        this.availableReflectors = configData.getBTEReflectors().getBTEReflector().stream()
                .map(EnigmaReflector::new)
                .collect(Collectors.toList());

        for (EnigmaReflector reflector : this.availableReflectors) {
            reflector.setAlphabet(this.alphabet);
        }
    }

    public boolean isInAlphabet(char ch) {
        return alphabet.contains(ch);
    }

    public int getAlphabetSize() {
        return alphabet.size();
    }

    public List<EnigmaRotor> getAvailableRotors() {
        return availableRotors;
    }

    public List<EnigmaReflector> getAvailableReflectors() {
        return availableReflectors;
    }

    public int getRotorCount() {
        return rotorCount;
    }
}