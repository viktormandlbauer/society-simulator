package at.fhtw.society.backend.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "deepinfra")
@Setter
@Getter
public class DeepinfraProperties {
    private String apiKey;
    private String model;
    private String url;
}
