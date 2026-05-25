package habitTracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekRequest {
    private String model;
    private double temperature;
    private List<DeepSeekMessage> messages;
}