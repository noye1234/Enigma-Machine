package patmal.course.enigma.engine.MachineModel;

import java.io.Serializable;

public class MachineData implements Serializable {
    private int numRotors;
    private int numReflectors;
    private int sumOfMassages;
    private String currentCode;
    private String origCode;


    //private int numPlugPairs;
    public MachineData(){}

    public MachineData(int numRotors, int numReflectors, int sumOfMassages, String origCode , String currentCode) {
        this.numRotors = numRotors;
        this.numReflectors = numReflectors;
        this.sumOfMassages = sumOfMassages;
        this.currentCode = currentCode;
        this.origCode = origCode;
    }

    public String toString(){
        String origCodePrint=origCode;
        String currCodePrint=currentCode;
        if(origCode==null){
            origCodePrint="There is no original code.";
        }
        if(currentCode==null){
            currCodePrint="There is no current code.";
        }


        return "\nMachine Data:\n" +
               "Number of Rotors: " + numRotors + "\n" +
               "Number of Reflectors: " + numReflectors + "\n" +
               "Total Messages Processed: " + sumOfMassages + "\n" +
               "Original Code: " + origCodePrint + "\n" +
               "Current Code: " + currCodePrint + "\n";
    }

}
