package at.fhtw.society.backend.lobby.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SocketIOConfig {

    @Value("${socketio.host:localhost}")
    private String host;

    @Value("${socketio.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // CORS configuration - adjust as needed for your frontend
        config.setOrigin("*");

        // Additional configuration
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);

        // Configure Jackson to support Java 8 date/time types
        config.setJsonSupport(new com.corundumstudio.socketio.protocol.JacksonJsonSupport(new JavaTimeModule()));

        return new SocketIOServer(config);
    }
}
