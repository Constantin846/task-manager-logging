package tk.project.taskmanager.logging.aspect.config;

import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "task-manager.logging")
public class LoggingAspectConfigProperties {
    private static final Level DEFAULT_RECORDING_LEVEL = Level.INFO;
    private final Level recordingLevel;

    public LoggingAspectConfigProperties(String recordingLevel) {
        this.recordingLevel = Objects.isNull(recordingLevel) || recordingLevel.isBlank() ?
                DEFAULT_RECORDING_LEVEL : Level.valueOf(recordingLevel);
    }

    public Level getRecordingLevel() {
        return recordingLevel;
    }
}
