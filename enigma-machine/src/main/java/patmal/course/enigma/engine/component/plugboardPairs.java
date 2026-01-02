package patmal.course.enigma.engine.component;

import java.util.HashMap;
import java.util.Map;

public class plugboardPairs {
    private Map<Character, Character> plugboardPairs = new HashMap<>();

    public plugboardPairs() {
    }
    public plugboardPairs(plugboardPairs other) {
        this.plugboardPairs = new HashMap<>(other.plugboardPairs);
    }

    public void addPair(char a, char b) {
        plugboardPairs.put(a, b);
        if (a!=b)
          plugboardPairs.put(b, a);
    }

    public char swap(char ch) {
        return plugboardPairs.getOrDefault(ch, ch);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Character> entry : plugboardPairs.entrySet()) {
            if (entry.getKey() <= entry.getValue()) { // To avoid duplicates
                sb.append(entry.getKey()).append("|").append(entry.getValue()).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1); // Remove last comma

        return sb.toString().trim();
    }

    boolean isEmpty() {
        return plugboardPairs.isEmpty();
    }

}
