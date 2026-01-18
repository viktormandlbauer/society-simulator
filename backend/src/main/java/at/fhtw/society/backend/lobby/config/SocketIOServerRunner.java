package at.fhtw.society.backend.lobby.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    public SocketIOServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) {
        server.start();
        log.info("SocketIO server started on port: {}", server.getConfiguration().getPort());
    }
}
