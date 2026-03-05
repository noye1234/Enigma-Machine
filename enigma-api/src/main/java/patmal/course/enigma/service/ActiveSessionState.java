package patmal.course.enigma.service;

import patmal.course.enigma.engine.EngineImpl;

public class ActiveSessionState {

    private final String sessionId;
    private final String machineName;
    private final EngineImpl sessionMachine;

    public ActiveSessionState(String sessionId, String machineName,EngineImpl sessionMachine ) {
        this.sessionId = sessionId;
        this.machineName = machineName;
        this.sessionMachine = sessionMachine;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMachineName() {
        return machineName;
    }

    public EngineImpl getEngine() {
        return sessionMachine;
    }
}