package at.fhtw.society.backend.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "deepinfra")
public class DeepinfraProperties {
    private String apiKey;
    private String model;
    private String url;

    public String getUrl() {return this.url;}
    public void setUrl(String url) {this.url = url;}

    public String getKey() { return apiKey; }
    public void setKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
