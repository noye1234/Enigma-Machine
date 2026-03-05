package patmal.course.enigma.engine;

import jakarta.xml.bind.JAXBException;
import patmal.course.enigma.engine.MachineModel.MachineData;
import patmal.course.enigma.LoadManager.*;
import patmal.course.enigma.loader.schema.*;

import patmal.course.enigma.engine.component.*;
import patmal.course.enigma.engine.MachineModel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EngineImpl implements Engine, Serializable {

    private final LoadManager loadManager;
    private EnigmaMachineModel machineModel;
    private ArrayList<Code> code= new ArrayList<>();


    private int abcSize;
    private boolean isConfigValid = false;
    private boolean is_code_initialized = false;

    public EngineImpl() {
        this.loadManager = new LoadManagerImpl();
    }

    private boolean validateSequentialIds(Set<Integer> ids, int expectedCount) {
        if (ids.size() != expectedCount) return false;
        for (int i = 1; i <= expectedCount; i++) {
            if (!ids.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateRomanIds(Set<String> ids) {
        String[] requiredIds = {"I", "II", "III", "IV", "V"};

        if (ids.size() > 5 || ids.isEmpty()) return false;

        List<String> validSet = new ArrayList<>(Arrays.asList(requiredIds).subList(0, ids.size()));

        for (String id : ids) {
            if (!validSet.contains(id)) return false;
        }
        return true;
    }

    /** Rule 2: Checks if the size of the ABC alphabet is even. */
    private boolean rule2(BTEEnigma config) {
        // Validation check for nulls
        if (config == null || config.getABC() == null) {
            return false;
        }
        // Check if the length of the ABC string is even
        return config.getABC().trim().length() % 2 == 0;
    }

    /** Rule 3: Checks if at least 3 rotors are defined. */
    private boolean rule3(BTEEnigma config) {
        // Defensive check for nulls in the JAXB structure
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        // Check if the size of the rotors list is at least 3
        return config.getBTERotors().getBTERotor().size() >= 3;
    }
    private boolean rule4(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        Set<Integer> rotorIds = new HashSet<>();
        for (BTERotor rotor : config.getBTERotors().getBTERotor()) {
            rotorIds.add(rotor.getId());
        }

        return validateSequentialIds(rotorIds, config.getBTERotors().getBTERotor().size());
    }
    private boolean rule5(BTEEnigma config) {
        // Defensive check: If the configuration structure is missing, treat as invalid (or handled by other rules)
        if (config == null || config.getBTERotors() == null) {
            return false;
        }

        List<BTERotor> rotors = config.getBTERotors().getBTERotor();

        for (BTERotor rotor : rotors) {

            if (rotor.getBTEPositioning() != null) {
                Set<String> leftSet = new HashSet<>();
                Set<String> rightSet = new HashSet<>();

                // Iterate over all mapping pairs (BTEPositioning) in the current rotor
                for (BTEPositioning pos : rotor.getBTEPositioning()) {

                    // Rule 5 violation check: If Set.add() returns false, the element is a duplicate.

                    // Check Left column uniqueness
                    if (!leftSet.add(pos.getLeft())) {
                        // Found a duplicate input mapping in this rotor. Validation fails immediately.
                        return false;
                    }

                    // Check Right column uniqueness
                    if (!rightSet.add(pos.getRight())) {
                        // Found a duplicate output mapping in this rotor. Validation fails immediately.
                        return false;
                    }
                }
            } else {
                // If a rotor has no mappings, that might be a failure mode,
                // but usually, the schema ensures mappings exist. We assume
                // missing mappings are invalid for the purpose of this rule.
                return false;
            }
        }

        // If the loop completes for all rotors without returning false, the mappings are unique.
        return true;
    }
    private boolean rule6(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTERotors() == null || config.getBTERotors().getBTERotor() == null) {
            return false;
        }

        int abcLength = config.getABC().length();

        for (BTERotor rotor : config.getBTERotors().getBTERotor()) {
            int notch = rotor.getNotch();
            if (notch < 0 || notch >= abcLength) {
                return false; // Invalid notch position found
            }
        }
        return true; // All notch positions are valid
    }
    private boolean rule7(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTEReflectors() == null || config.getBTEReflectors().getBTEReflector() == null) {
            return false;
        }

        Set<String> reflectorIds = new HashSet<>();
        for (BTEReflector reflector : config.getBTEReflectors().getBTEReflector()) {
            reflectorIds.add(reflector.getId());
        }

        return validateRomanIds(reflectorIds);

    }
    private boolean rule8(BTEEnigma config) {
        // Defensive null checks
        if (config == null || config.getBTEReflectors() == null || config.getBTEReflectors().getBTEReflector() == null) {
            return false;
        }

        for (BTEReflector reflector : config.getBTEReflectors().getBTEReflector()) {
            if (reflector.getBTEReflect() != null) {
                for (BTEReflect reflect : reflector.getBTEReflect()) {
                    if (reflect.getInput() == reflect.getOutput()) {
                        return false; // Found self-mapping
                    }
                }
            }
        }
        return true; // No self-mappings found
    }

    private List<String> validateConfiguration(BTEEnigma config) {
        List<String> errors = new ArrayList<>();

        if (config == null) {
            errors.add("Error: XML file is empty or structure is incorrect (JAXB loading failed).");
            return errors;
        }

        String abc = config.getABC();

        // בדיקה קריטית למניעת NullPointerExceptions
        if (config.getBTERotors() == null || config.getBTEReflectors() == null) {
            errors.add("Error: Missing required sections: BTE-Rotors or BTE-Reflectors.");
            return errors;
        }
        if (!rule2(config)) {
            errors.add("Error: length of the ABC string is not even");
        }
        if (!rule3(config)) {
            errors.add("Error: size of the rotors list is less then 3");
        }
        if (!rule4(config)) {
            errors.add("Error: rotor IDs are not sequential integers");
        }
        if (!rule5(config)) {
            errors.add("Error: one or more rotors have duplicate input/output mappings");
        }
        if (!rule6(config)) {
            errors.add("Error: one or more rotors have invalid notch positions");
        }
        if (!rule7(config)) {
            errors.add("Error: Reflector IDs are not valid sequential Roman numerals (I-V)");
        }
        if (!rule8(config)) {
            errors.add("Error: one or more reflectors have self-mapping (Input equals Output)");
        }

        List<BTERotor> rotors = config.getBTERotors().getBTERotor();

        List<BTEReflector> reflectors = config.getBTEReflectors().getBTEReflector();
        Set<String> reflectorIds = new HashSet<>();

        if (reflectors.isEmpty()) {
            errors.add("Error: Reflector definitions (BTEReflectors) are missing or empty.");
        }

        return errors;
    }

    public String getMachineName() {
        return this.machineModel.getMachineName();
    }
    public void setMachineName(String name) {
         this.machineModel.setMachineName(name);
    }


    @Override
    public List<String> loadXml(InputStream inputStream) {
        BTEEnigma tempJAXBConfig = null;
        List<String> errors = new ArrayList<>();

        try {
            tempJAXBConfig = loadManager.load(inputStream, BTEEnigma.class);

            errors = validateConfiguration(tempJAXBConfig);

            if (errors.isEmpty()) {
                this.machineModel = new EnigmaMachineModel(tempJAXBConfig);

                if (this.machineModel.getAvailableRotors().size() < this.machineModel.getRotorCount()) {
                    errors.add("Error: Not enough rotors defined for the machine configuration.");
                    return errors;
                }

                this.abcSize = this.machineModel.getAlphabetSize();
                this.isConfigValid = true;
                setMachineName(tempJAXBConfig.getName());
            } else {
                return errors;
            }
        } catch (JAXBException e) {
            errors.add("Error: XML parsing failed. Please ensure the file matches the schema. " + e.getMessage());
        } catch (Exception e) {
            errors.add("Error: An unexpected error occurred: " + e.getMessage());
        }

        return errors;
    }


    @Override
    public MachineData getMachineData(){
        Code releventCode=null;
        String origCodeStr=null;
        String currCodeStr=null;
        int massagesCount=0;
        if (!this.code.isEmpty()){
            releventCode = this.code.get(getCodeSize() - 1);
            origCodeStr=releventCode.toString(releventCode.getCopyRotors());
            currCodeStr= releventCode.toString(releventCode.getRotors());
            massagesCount=releventCode.getMessagesCount();
        }



        int sumOfRotor = machineModel.getAvailableRotors().size() ;
        int sumOfReflector = machineModel.getAvailableReflectors().size() ;

        MachineData machineData= new MachineData(sumOfRotor,sumOfReflector,massagesCount,
                origCodeStr,currCodeStr);

        return machineData;

    }


    public String part1(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ){
        String str=input[index[0]].trim();
        if (str==null || str.charAt(0)!='<'){
            return "Error: Invalid code format ";
        }
        str=str.substring(1); // remove the opening '<'

        int maxNumOfRotors=this.machineModel.getRotorCount();

        while (str.charAt(0)!='<' && (index[0]+1)<= maxNumOfRotors){
            try {

                Integer ID = Integer.parseInt(str.trim());
                order.add(index[0], ID);
                for (EnigmaRotor r : machineModel.getAvailableRotors()) {
                    if (r.getRotorId() == ID) {
                        rotors.put(ID, r);
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                return "Error: Invalid str ID format: " + str + "insert a number";
            }
            index[0]++;
            str=input[index[0]].trim();
            if (str==null){
                return "Error: Invalid code format ";
            }

        }
        if (str.charAt(0)!='<'){
            return "There is more rotors than the machine support ";
        }


        return null;

    }

    public String part2(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ){
        String str=input[index[0]].trim();
        if (str==null || str.charAt(0)!='<'){
            return "Invalid code format ";
        }
        str=str.substring(1); // remove the opening '<'

        char CharLetter;
        String error= validateStartingPosition(str,rotors);
        if (error!=null){
            return error;
        }

        int len=str.length();
        for (int i=0;i<len; i++){

            CharLetter = str.charAt(i);

            rotors.get(order.get(i)).setStartingPosition(CharLetter);

        }
        index[0]++;


        return null;
    }

    public String part3(Map<Integer,EnigmaRotor> rotors,ArrayList<Integer> order,String[] input,int[] index ) {
        EnigmaReflector reflector;
        String valueRome = input[index[0]].trim();
        if (valueRome.isEmpty() || valueRome.charAt(0)!= '<' || valueRome.length()!=2) {
            return "Invalid code format ";
        }
        valueRome=valueRome.substring(1);

        index[0]++;

        int valueNumRome = (int) (valueRome.charAt(0) - '0');


        String error=isValidReflector(valueRome);
        if (error!=null){
            return error;
        }

        if (valueRome.matches("\\d+")) {
            try {
                String rome = NumberRome.fromInt(valueNumRome).name();
                for (EnigmaReflector r : machineModel.getAvailableReflectors()) {
                    if (r.getId().equals(rome)) {
                        reflector = r;

                        reflector.setId(rome);
                        this.code.add(new Code(rotors, order, reflector));
                        return null;
                    }
                }
            } catch (NumberFormatException e) {
                return "Invalid str notch format: " + valueRome + "insert a number";
            }
        } else if (valueRome.matches("I|II|III|IV|V")) {
            try {
                for (EnigmaReflector r : machineModel.getAvailableReflectors()) {
                    if (r.getId().equals(valueRome)) {
                        reflector = r;

                        reflector.setId(valueRome);
                        this.code.add(new Code(rotors, order, reflector));
                        return null;
                    }
                }
            } catch (NumberFormatException e) {
                return "Invalid str notch format: " + valueRome + "insert a number/roman number I-V";
            }
        } else {
            return "Invalid str notch format: " + valueRome + "insert a number or roman number I-V";

        }

        return null;


    }

    public String part4(ArrayList<Code> code,String[] input,int[] index ){
        int len=input.length;

        if (index[0]>=len){
            return null;
        }
        String str=input[index[0]].trim();
        str=str.substring(1);

        char firstChar;
        char secondChar;

        while (index[0]<len){
            firstChar=str.charAt(0);
            if (str.length()<3){
                this.code.remove(getCodeSize()-1);
                return "Invalid plugboard pair format: " + str + ". Each pair must consist of two characters.";
            }
            secondChar=str.charAt(2);
            this.code.get(getCodeSize()-1).addPlugboardPair(firstChar,secondChar);
            index[0]++;
            if (index[0]>=len){
                return null;
            }
            str=input[index[0]].trim();
        }
        return null;
        // the rest is ignored
    }

    public String isValidReflector(String valueRome){
        if (valueRome.matches("I|II|III|IV|V")==false && valueRome.matches("\\d+")==false){
            return "Reflector ID "+valueRome+" is not valid. It should be a Roman numeral (I-V) or a number.";
        }


        for (EnigmaReflector r: machineModel.getAvailableReflectors()){
            if (r.getId().equals(valueRome) || r.getId().equals(NumberRome.fromInt((int)(valueRome.charAt(0)-'0')).name())){
                return null;
            }

        }
        return "Reflector ID "+valueRome+" does not exist in the machine configuration.";
    }

    public String validateStartingPosition(String str,Map<Integer,EnigmaRotor> rotors){
        int len=str.length();
        str=str; //.toUpperCase();

        for (int i=0;i<len; i++){
            char CharLetter = str.charAt(i);
            if (!machineModel.isInAlphabet(CharLetter)){
                return "Starting position "+CharLetter+" is not in the machine's alphabet.";
            }
        }
        if (len!= rotors.size()){
            return "Number of starting positions "+len+" does not match the number of rotors in the machine "+ rotors.size() +".";
        }
        return null;
    }


    @Override
    public String codeManual(String codeInput) {
        if (!isConfigValid)
            return "Error: Machine configuration is not initialized. Please load a valid configuration before setting up the code.";

        String cleanedInput = codeInput.replaceAll("\\s", "");

        String[] rawInput = cleanedInput.split("[,(,),>]");

        String[] finalInput = Arrays.stream(rawInput)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (finalInput.length < 3) {
            return "Invalid code format: Not enough parts to define rotors, positions, and reflector.";
        }

        ArrayList<Integer> order = new ArrayList<>();
        Map<Integer,EnigmaRotor> rotors= new HashMap<>();

        int[] index = new int[1];
        index[0]=0;
        int j=-1;

        String error=part1(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }
        error=part2(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }

        error=part3(rotors,order,finalInput,index);
        if (error!=null){
            return error;
        }

        this.code.get(getCodeSize()-1).setAlphabet(machineModel.getAlphabet());
        this.is_code_initialized=true;
        return null;
    }


    @Override
    public Code codeAutomatic() {
        if (!isConfigValid)
            throw new IllegalStateException("Machine configuration is not initialized. Please load a valid configuration before setting up the code.");

        List<Character> alphabet = this.machineModel.getAlphabet();
        char firstChar,secondChar;
        Random random = new Random();
        int alphabetSize = this.machineModel.getAlphabetSize();
        int numOfPlugboardPairs = random.nextInt(alphabetSize / 2 + 1); // up to half the alphabet size

        setRandomRotorAndReflector();

        Code currentCode = this.code.get(getCodeSize() - 1);

        currentCode.setAlphabet(alphabet);

        for (int i=0; i<numOfPlugboardPairs; i++) {
            int shuffleIndex =random.nextInt(alphabetSize);
            firstChar =  alphabet.get(shuffleIndex);

            while (currentCode.isCharInPlugboard(firstChar) ) {
                shuffleIndex= (shuffleIndex+1)% alphabetSize;
                firstChar = alphabet.get(shuffleIndex);
            }

            shuffleIndex =random.nextInt(alphabetSize);
            secondChar = alphabet.get(shuffleIndex);

            while (currentCode.isCharInPlugboard(secondChar) || firstChar==secondChar) {
                shuffleIndex= (shuffleIndex+1)% alphabetSize;
                secondChar = alphabet.get(shuffleIndex);
            }
            currentCode.addPlugboardPair(firstChar, secondChar);
        }
        this.is_code_initialized=true;
        return currentCode;
    }

    public void setRandomRotorAndReflector() {
        Map<Integer,EnigmaRotor> rotors= new HashMap<>();
        EnigmaReflector reflector;
        Random random = new Random();
        int alphabetSize = this.machineModel.getAlphabetSize();
        int requiredRotorCount = this.machineModel.getRotorCount();

        List<Integer> rotorIDs = this.machineModel.getAvailableRotors().stream()
                .map(EnigmaRotor::getRotorId)
                .collect(Collectors.toList());

        Collections.shuffle(rotorIDs, random);
        List<Integer> activeRotorOrder = new ArrayList<>(rotorIDs.subList(0, requiredRotorCount));

        for ( Integer rotorID : activeRotorOrder ) {
            for (EnigmaRotor r : machineModel.getAvailableRotors()) {
                if (r.getRotorId() == rotorID) {
                    rotors.put(rotorID, new EnigmaRotor(r));
                    rotors.get(rotorID).setStartingPosition(r.getRight().charAt(random.nextInt(alphabetSize)));
                    break;
                }
            }
        }

        List<Integer> reflectorIDs = this.machineModel.getAvailableReflectors().stream()
                .map(EnigmaReflector::getId)
                .map(romanId -> NumberRome.valueOf(romanId).toInt())
                .collect(Collectors.toList());
        Collections.shuffle(reflectorIDs, random);
        int selectedReflectorID = reflectorIDs.get(0);

        for ( EnigmaReflector r : machineModel.getAvailableReflectors() ) {
            if (r.getId().equals(NumberRome.fromInt(selectedReflectorID).name())) {
                reflector = new EnigmaReflector(r);
                reflector.setId(NumberRome.fromInt(selectedReflectorID).name());
                this.code.add(new Code(rotors, activeRotorOrder, reflector));
                break;
            }
        }
    }


    public int getCodeSize(){
        if (this.code==null){
            return 0;
        }
        return this.code.size();
    }

    @Override
    public List<String> process(String message) {
        List<String> errorResponse = new ArrayList<>();
        if (!this.isConfigValid || !this.is_code_initialized) {
            return errorResponse; // Return empty list to indicate no processing can be done
        }

         String validationError = validateMessage(message);
         if (validationError != null) {
             errorResponse.add(validationError);
             return errorResponse;
         }

        restartCode();

        Long time_start = System.currentTimeMillis();

        String processed;

        processed =  this.code.get(getCodeSize()-1).processMessage(message);

        Long time_end = System.currentTimeMillis();
        this.code.get(getCodeSize()-1).addOrginalMassage(message);
        this.code.get(getCodeSize()-1).addOutMassage(processed);
        this.code.get(getCodeSize()-1).addTime(time_end - time_start);

        List<String> processedMessagesAndTime = new ArrayList<>();
        processedMessagesAndTime.add(processed);
        processedMessagesAndTime.add(String.valueOf(time_end - time_start));

        return processedMessagesAndTime;

    }

    @Override
    public List<String> statistics() {
        List<String> stats = new ArrayList<>();
        for (Code c : this.code){
            stats.add(c.toString(c.getCopyRotors()));
            stats.add(c.printMassagesData());
        }
        return stats;
    }
    @Override
    public void exit(){
        this.machineModel.changeRunMode(false);
    }


    @Override
    public String restartCode(){
        if (!this.isConfigValid || !this.is_code_initialized) {
            return "Machine configuration or code is not initialized.";
        }

        this.code.get(getCodeSize()-1).restart_code();
        return null;

    }


    public String getCurrentCode(){
        return this.code.get(getCodeSize()-1).toString(this.code.get(getCodeSize()-1).getRotors());
    }

    public String saveMachineState(String filePath){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath + ".dat"))) {
            out.writeObject(this);
            return "Machine state saved successfully to " + filePath + ".dat";
        } catch (IOException e) {
            return "Error saving machine state: " + e.getMessage();
        }
    }
    public String loadMachineState(String filePath){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            EngineImpl loadedEngine = (EngineImpl) in.readObject();
            this.machineModel = loadedEngine.machineModel;
            this.code = loadedEngine.code;
            this.abcSize = loadedEngine.abcSize;
            this.isConfigValid = loadedEngine.isConfigValid;


            return null;
        } catch (IOException | ClassNotFoundException e) {
            return "Error loading machine state: " + e.getMessage();
        }
    }

    public String validateMessage(String message){
        StringBuilder invalidChars = new StringBuilder();
        int len=message.length();
        message=message.trim();

        if (message.length()==0){
            return "Error: Message is empty.";
        }

        for (int i=0;i<len;i++){
            if(!machineModel.isInAlphabet(message.charAt(i))){
                if (invalidChars.indexOf(String.valueOf(message.charAt(i))) == -1) {
                    invalidChars.append(message.charAt(i));
                }
            }
        }
        if (invalidChars.length() > 0) {
            return "Error: Message contains invalid characters not in the machine's alphabet: " + invalidChars.toString();
        }
        return null;
    }

    public String addPlugboardPairs(String pairsInput){
        return this.code.get(getCodeSize()-1).createPlugBoardPairs(pairsInput);
    }

    public EngineImpl createCopy() {
        EngineImpl copy = new EngineImpl();
        copy.machineModel = this.machineModel;
        copy.code = new ArrayList<>();
        copy.abcSize = this.abcSize;
        copy.isConfigValid = this.isConfigValid;
        copy.is_code_initialized=false;
        return copy;
    }

    public List<EnigmaReflector> getAvailableReflectors() {
        return this.machineModel.getAvailableReflectors();
    }
    public List<Character> getABC() {
        return this.machineModel.getAlphabet();
    }
    public List<EnigmaRotor> getAvailableRotors(){
        return this.machineModel.getAvailableRotors();
    }
    public String getCurrentRotorsPositionCompact(char ch){
        return this.code.get(getCodeSize()-1).getCurrentRotorsPositionCompact(ch);
    }

    public String getReflectorId() {
        return this.code.get(getCodeSize()-1).getReflectorId();
    }

    public  Map<Character, Character> getPlugboardPairs(){
        return this.code.get(getCodeSize()-1).getPlugboardPairs();
    }


    public Map<Integer, EnigmaRotor> getCurrentRotors() {
        return code.getLast().getCurrentRotors();
    }
    public Map<Integer,EnigmaRotor> getOrginalRotors() {
        return code.getLast().getOrginalRotors();
    }

}