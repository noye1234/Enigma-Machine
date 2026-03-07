package patmal.course.enigma.ProcessRequestDTO.ai;
public class AiRequestDTO {
    private String query;

    public AiRequestDTO() {}

    public AiRequestDTO(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}