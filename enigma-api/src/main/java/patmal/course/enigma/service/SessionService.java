package patmal.course.enigma.service;

import org.springframework.stereotype.Service;
import patmal.course.enigma.engine.EngineImpl;
import patmal.course.enigma.engine.MachineManager.MachineManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<String, ActiveSessionState> activeSessions = new ConcurrentHashMap<>();
    private final MachineManager machineManager;

    public SessionService(MachineManager machineManager) {
        this.machineManager = machineManager;
    }
    public String createNewSession(String machineName) {

        if (!machineManager.is_engine_exist(machineName)) {
            throw new RuntimeException("Unknown machine name: " + machineName);
        }

        EngineImpl sessionEngine = machineManager.getMachineCopy(machineName);

        String newSessionId = UUID.randomUUID().toString();

        ActiveSessionState sessionState = new ActiveSessionState(newSessionId, machineName, sessionEngine);

        activeSessions.put(newSessionId, sessionState);

        return newSessionId;
    }

    public ActiveSessionState getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public boolean deleteSession(String sessionId) {
        return activeSessions.remove(sessionId) != null;
    }
}