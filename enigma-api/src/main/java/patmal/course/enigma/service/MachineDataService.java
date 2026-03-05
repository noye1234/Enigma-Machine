package patmal.course.enigma.service;

import org.springframework.stereotype.Service;
import patmal.course.enigma.ProcessRequestDTO.*;
import patmal.course.enigma.engine.MachineModel.MachineData;
import patmal.course.enigma.engine.component.EnigmaRotor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MachineDataService {

    private final SessionService sessionService;

    public MachineDataService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public ConfigStatusResponse getMachineConfigStatus(String sessionId, boolean verbose) {
        ActiveSessionState sessionState = sessionService.getSession(sessionId);

        if (sessionState == null) {
            throw new RuntimeException("Session not found or expired.");
        }

        MachineData dataFromEngine = sessionState.getEngine().getMachineData();

        ConfigStatusResponse response = new ConfigStatusResponse();
        response.totalRotors = dataFromEngine.getSumOfRotor();
        response.totalReflectors = dataFromEngine.getSumOfReflector();
        response.totalProcessedMessages = dataFromEngine.getMassagesCount();
        response.originalCodeCompact = dataFromEngine.getOrigCodeStr();
        response.currentRotorsPositionCompact = sessionState.getEngine().getCurrentRotorsPositionCompact('(');

        if (verbose) {

            Map<Character, Character> plugboardPairs= sessionState.getEngine().getPlugboardPairs();
            Map<Integer,EnigmaRotor> currentRotors = sessionState.getEngine().getCurrentRotors();
            Map<Integer,EnigmaRotor> originalRotors = sessionState.getEngine().getOrginalRotors();

            EnigmaCodeStructure origCode = new EnigmaCodeStructure();
            origCode.reflector = sessionState.getEngine().getReflectorId();
            origCode.rotors = new ArrayList<>();

            origCode.plugs = new ArrayList<>();


            response.originalCode = origCode;

            EnigmaCodeStructure currCode = new EnigmaCodeStructure();
            currCode.reflector = sessionState.getEngine().getReflectorId();
            currCode.rotors = new ArrayList<>();

            currCode.plugs = new ArrayList<>();

            for (Map.Entry<Character, Character> entry : plugboardPairs.entrySet()) {
                PlugConnection pair = new PlugConnection();
                pair.plug1 = entry.getKey();
                pair.plug2 = entry.getValue();
                currCode.plugs.add(pair);
                origCode.plugs.add(pair);
            }

            for (Map.Entry<Integer,EnigmaRotor> entry : originalRotors.entrySet()) {
                RotorSelectionWithNotch rotorInfo = new RotorSelectionWithNotch();
                rotorInfo.rotorNumber = entry.getValue().getRotorId();
                rotorInfo.notchDistance = 0;
                rotorInfo.rotorPosition= entry.getValue().getStartingPosition();
                origCode.rotors.add(rotorInfo);
            }

            for (Map.Entry<Integer,EnigmaRotor> entry : currentRotors.entrySet()) {
                RotorSelectionWithNotch rotorInfo = new RotorSelectionWithNotch();
                rotorInfo.rotorNumber = entry.getValue().getRotorId();
                rotorInfo.notchDistance = entry.getValue().getDistanceFromWindow();
                rotorInfo.rotorPosition= entry.getValue().getStartingPosition();
                currCode.rotors.add(rotorInfo);
            }
            response.currentRotorsPosition = currCode;
        }

        return response;
    }
}