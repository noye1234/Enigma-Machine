package patmal.course.enigma.ProcessRequestDTO.ai;

public class AiResponseDTO {
    private String answer;
    private String sql;

    public AiResponseDTO(String answer, String sql) {
        this.answer = answer;
        this.sql = sql;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}