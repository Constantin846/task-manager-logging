package tk.project.taskmanager.logging.aspect.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tk.project.taskmanager.logging.aspect.LoggingAspect;

@Configuration
@ConditionalOnProperty(name = "task-manager.logging.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(LoggingAspectConfigProperties.class)
public class LoggingAspectAutoConfig {
    private final LoggingAspectConfigProperties loggingProperties;

    public LoggingAspectAutoConfig(LoggingAspectConfigProperties loggingProperties) {
        this.loggingProperties = loggingProperties;
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect(loggingProperties.getRecordingLevel());
    }
}
