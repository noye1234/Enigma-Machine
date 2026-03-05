package patmal.course.enigma.ProcessRequestDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoadResponse {
    private boolean success;
    private String name;
    private String error;

    public LoadResponse(boolean success, String name, String error) {
        this.success = success;
        this.name = name;
        this.error = error;
    }
    public boolean isSuccess() { return success; }
    public String getName() { return name; }
    public String getError() { return error; }
}