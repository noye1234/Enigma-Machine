package patmal.course.enigma.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    // חשוב מאוד: אמרנו לו לייצר רק שאילתות SELECT כדי שלא ימחק לנו נתונים בטעות!
    private static final String SYSTEM_PROMPT_SQL = """
            You are an expert PostgreSQL developer. 
            Given a user's question, your task is to write a valid PostgreSQL SELECT query to answer it.
            
            Here is the structure of the database:
            1. Table 'machines': Columns: id (integer), name (varchar), abc (varchar), rotors_count (integer).
            2. Table 'processing': Columns: id (integer), time (varchar), machine_id (integer), code (varchar), input (varchar), output (varchar), session_id (varchar).
            3. Table 'machines_rotors': Columns: id (integer), machine_id (integer), rotor_id (integer), notch (integer), wiring_left (varchar), wiring_right (varchar).
            
            Rules:
            - Return ONLY the raw SQL SELECT query.
            - Do not add markdown formatting.
            - NEVER write INSERT, UPDATE, or DELETE queries.
            """;

    private final JdbcTemplate jdbcTemplate;

    // הזרקת ה-JdbcTemplate שמאפשר לנו לדבר ישירות עם מסד הנתונים
    public AiService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. קריאה ל-AI כדי לקבל שאילתת SQL
    public String generateSqlFromQuery(String userQuery) {
        return callOpenAi(SYSTEM_PROMPT_SQL, userQuery);
    }

    // 2. הרצת השאילתה מול ה-DB
    public String executeDynamicSql(String sql) {
        try {
            // שולף רשימה של שורות, כל שורה היא מפה של עמודה->ערך
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            if (rows.isEmpty()) {
                return "No results found in the database.";
            }

            // ממיר את התוצאות לטקסט קריא שה-AI יוכל להבין
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

    // 3. קריאה שניה ל-AI כדי שייצר תשובה אנושית מהנתונים
    public String generateFinalAnswer(String userQuery, String rawDbData) {
        String prompt = "The user asked: '" + userQuery + "'. " +
                "The database returned the following raw data: \n" + rawDbData + "\n\n" +
                "Formulate a short, friendly, natural language answer. " +
                "Do not mention the database, SQL, or technical column names in your answer. Just answer the question directly based on the data.";

        return callOpenAi("You are a helpful assistant.", prompt);
    }

    // מתודה גנרית שמבצעת את הבקשה האמיתית ל-OpenAI (כדי לא לשכפל קוד)
    private String callOpenAi(String systemPrompt, String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.0);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_API_URL, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

            return message.get("content").toString().trim();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API call failed: " + e.getMessage());
        }
    }
}