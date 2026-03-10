package patmal.course.enigma.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final String SYSTEM_PROMPT_SQL = """
            You are an expert PostgreSQL developer. 
            Given a user's question, your task is to write a valid PostgreSQL SELECT query to answer it.
            
            Here is the structure of the database:
            1. Table 'machines': Columns: id (integer), name (varchar), rotors_count (integer), abc (varchar).
            2. Table 'machines_reflectors': Columns: id (integer), machine_id (integer), reflector_id (varchar), input (varchar), output (varchar).
            3. Table 'machines_rotors': Columns: id (integer), machine_id (integer), rotor_id (integer), notch (integer), wiring_right (varchar), wiring_left (varchar).
            4. Table 'processing': Columns: id (integer), machine_id (integer), session_id (varchar), code (varchar), input (varchar), output (varchar), time (varchar).
            
            Crucial Data Logic:
            - A component's uniqueness is defined by BOTH its specific ID and its machine_id. 
            - For example, two different machines might both have a reflector with reflector_id 'I'. Therefore, to find distinct reflectors or distinct rotors, you MUST look at the unique combination of (machine_id, reflector_id) or (machine_id, rotor_id).
            
            Rules:
            - Return ONLY the raw SQL SELECT query.
            - Do NOT wrap the query in markdown blocks like ```sql ... ```. Just the query text.
            - NEVER write INSERT, UPDATE, DROP, or DELETE queries.
            """;

    private final JdbcTemplate jdbcTemplate;

    public AiService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateSqlFromQuery(String userQuery) {
        String rawSql = callAi(SYSTEM_PROMPT_SQL, userQuery, 0.0);
        return rawSql.replace("```sql", "").replace("```", "").trim();
    }

    public String executeDynamicSql(String sql) {
        try {
            if (!sql.toUpperCase().startsWith("SELECT")) {
                return "Security Error: Only SELECT queries are permitted.";
            }

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            if (rows.isEmpty()) {
                return "No results found in the database.";
            }

            StringBuilder resultText = new StringBuilder();
            for (Map<String, Object> row : rows) {
                resultText.append(row.toString()).append("\n");
            }
            return resultText.toString().trim();

        } catch (Exception e) {
            System.err.println("Database error executing dynamic SQL: " + e.getMessage());
            return "Error executing SQL: " + e.getMessage();
        }
    }

    public String generateFinalAnswer(String userQuery, String rawDbData) {
        String prompt = "The user asked: '" + userQuery + "'. " +
                "The database returned the following raw data: \n" + rawDbData + "\n\n" +
                "Formulate a short, friendly, natural language answer. " +
                "Do not mention the database, SQL, or technical column names in your answer. Just answer the question directly based on the data.";

        return callAi("You are a helpful submarine commander assistant.", prompt, 0.7);
    }

    private String callAi(String systemPrompt, String userMessage, double temperature) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("temperature", temperature);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return message.get("content").toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("API call failed: " + e.getMessage());
        }
    }
}