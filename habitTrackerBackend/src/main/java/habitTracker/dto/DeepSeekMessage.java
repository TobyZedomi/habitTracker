package habitTracker.dto;

import lombok.Data;

@Data
public class DeepSeekMessage {
    private String role;
    private String content;

    public DeepSeekMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
