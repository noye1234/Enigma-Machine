package patmal.course.enigma.service;

import org.springframework.stereotype.Service;

import patmal.course.enigma.ProcessRequestDTO.ManualCodeRequest;
import patmal.course.enigma.ProcessRequestDTO.PlugConnection;
import patmal.course.enigma.ProcessRequestDTO.RotorSelection;
import patmal.course.enigma.engine.component.Code;

@Service
public class CodeService {

    private final SessionService sessionService;
    private final SessionService sessionManager;

    public CodeService(SessionService sessionService, SessionService sessionManager) {
        this.sessionService = sessionService;
        this.sessionManager = sessionManager;
    }

    public void codeAutomatic(String sessionId) {
        ActiveSessionState sessionState = sessionService.getSession(sessionId);

        if (sessionState == null) {
            throw new RuntimeException("Session not found or expired.");
        }

        Code newConfig = sessionState.getEngine().codeAutomatic();

    }

    public String codeManually(ManualCodeRequest request) {
        ActiveSessionState sessionState = sessionService.getSession(request.sessionID);
        if (sessionState == null) {
            throw new RuntimeException("Session not found or expired.");
        }

        StringBuilder ids = new StringBuilder("<");
        StringBuilder positions = new StringBuilder("<");

        for (int i = 0; i < request.rotors.size(); i++) {
            RotorSelection rotor = request.rotors.get(i);
            ids.append(rotor.rotorNumber);
            positions.append(rotor.rotorPosition);

            if (i < request.rotors.size() - 1) {
                ids.append(",");
                positions.append(",");
            }
        }
        ids.append(">");
        positions.append(">");
        String refStr = "<" + request.reflector + ">";

        String oldFormatInput = ids.toString() + positions.toString() + refStr;

        String error = sessionState.getEngine().codeManual(oldFormatInput);

        if (error != null) {
            throw new RuntimeException("Error in manual coding: " + error);
        }

        if (request.plugs != null && !request.plugs.isEmpty()) {
            StringBuilder plugboardInput = new StringBuilder();
            for (PlugConnection plug : request.plugs) {
                plugboardInput.append(plug.plug1).append(plug.plug2);
            }

            String plugboardError = sessionState.getEngine().addPlugboardPairs(plugboardInput.toString());
            if (plugboardError != null) {
                throw new RuntimeException("Error in plugboard pairs: " + plugboardError);
            }
        }

        return "Coded Message: " + sessionState.getEngine().getCurrentCode();
    }

    public String restartCode(String sessionID){
        ActiveSessionState activeSessionState = sessionManager.getSession(sessionID);
        if (activeSessionState == null) {
            throw new RuntimeException("Session not found for sessionID: " + sessionID);
        }
        String error=activeSessionState.getEngine().restartCode();
        if (error != null) {
            throw new RuntimeException(error);
        }
        return "Code restarted. Current code: " + activeSessionState.getEngine().getCurrentCode();
    }
}
