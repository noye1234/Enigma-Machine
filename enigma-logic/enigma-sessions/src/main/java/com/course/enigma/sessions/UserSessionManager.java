package com.course.enigma.sessions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import patmal.course.enigma.engine.MachineManager.MachineManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@SessionScope // נוצר מופע חדש לכל משתמש
public class UserSessionManager {

    private final List<String> currentMachineNames;
    private final Map<String, Boolean> isConfigValid;
    private final Map<String, Boolean> isCodeDefined; // החלפנו את השם command_3_or_4 למשהו ברור יותר
    private final MachineManager manager;

    @Autowired
    public UserSessionManager(MachineManager manager) {
        this.manager = manager;
        this.currentMachineNames = new ArrayList<>();
        this.isConfigValid = new HashMap<>();
        this.isCodeDefined = new HashMap<>();
    }

    // Getters
    public MachineManager getManager() { return manager; }
    public List<String> getCurrentMachineNames() { return currentMachineNames; }

    // Helper methods to manage state
    public boolean isMachineConfigured(String name) {
        return isConfigValid.getOrDefault(name, false);
    }

    public void setMachineConfigured(String name, boolean status) {
        isConfigValid.put(name, status);
    }

    public boolean isCodeDefined(String name) {
        return isCodeDefined.getOrDefault(name, false);
    }

    public void setCodeDefined(String name, boolean status) {
        isCodeDefined.put(name, status);
    }

    public void clearCurrentMachineNames() {
        currentMachineNames.clear();
    }

    public void addMachineName(String name) {
        currentMachineNames.add(name);
    }

}