package patmal.course.enigma.engine.MachineModel;

import patmal.course.enigma.loader.schema.*;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import patmal.course.enigma.engine.component.*;

public class EnigmaMachineModel implements Serializable {

    private final List<Character> alphabet;
    private final List<EnigmaRotor> availableRotors; // משתמש ב-EnigmaRotor
    private final List<EnigmaReflector> availableReflectors; // משתמש ב-EnigmaReflector
    private final int rotorCount;
    private boolean run=true;
    private String machineName;

    public EnigmaMachineModel(List<Character> alphabet, List<EnigmaRotor> availableRotors, List<EnigmaReflector> availableReflectors, int rotorCount){
        this.alphabet=alphabet;
        this.availableRotors=availableRotors;
        this.availableReflectors=availableReflectors;
        this.rotorCount=rotorCount;
    }

    public List<Integer> getRotorsIdList() {
        return availableRotors.stream()
                .map(EnigmaRotor::getRotorId)
                .collect(Collectors.toList());
    }
    public List<String> getReflectorsIdList() {
        return availableReflectors.stream()
                .map(EnigmaReflector::getId)
                .collect(Collectors.toList());
    }
    public String getAlphabetAsString() {
        StringBuilder sb = new StringBuilder();
        for (Character ch : alphabet) {
            sb.append(ch);
        }
        return sb.toString();
    }
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

    public String getMachineName() {
        return machineName;
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

    public void setMachineName(String name) {

        this.machineName = name.trim();
    }


}