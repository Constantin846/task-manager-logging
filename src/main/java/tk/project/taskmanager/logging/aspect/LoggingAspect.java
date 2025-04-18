package tk.project.taskmanager.logging.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Aspect
public class LoggingAspect {
    private final LoggingEventBuilder logger;

    public LoggingAspect(Level recordingLevel) {
        Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
        this.logger = logger.atLevel(recordingLevel);

        logger.info(String.format("%s initialized with recording level: %s",
                this.getClass().getSimpleName(), recordingLevel));
    }

    @Before("@annotation(tk.project.taskmanager.logging.aspect.annotation.LogStartMethod)" +
            " || @annotation(tk.project.taskmanager.logging.aspect.annotation.LogCoverMethod)")
    public void loggingBefore(JoinPoint joinPoint) {
        logger.log(String.format("Start method %s with parameters: %s",
                joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs())));
    }

    @AfterThrowing(
            pointcut = "(@annotation(tk.project.taskmanager.logging.aspect.annotation.LogException)" +
                    " || @annotation(tk.project.taskmanager.logging.aspect.annotation.LogCoverMethod))",
            throwing = "exception"
    )
    public void loggingException(JoinPoint joinPoint, Exception exception) {
        logger.log(String.format("Exception was thrown by method %s with parameters: %s",
                joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs())));
        logger.log(String.format("Exception %s contains a message: %s",
                exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @AfterReturning(
            pointcut = "(@annotation(tk.project.taskmanager.logging.aspect.annotation.LogCompleteMethod)" +
                    " || @annotation(tk.project.taskmanager.logging.aspect.annotation.LogCoverMethod))",
            returning = "result"
    )
    public void loggingFinishMethod(JoinPoint joinPoint, Object result) {
        logger.log(String.format("Method %s was completed successfully with the result: %s",
                joinPoint.getSignature().toShortString(), result));
    }

    /**
     * {@code @Order(Integer.MAX_VALUE - 2)}
     *  <p>The annotation states that this advice covers around all the others. For example @Transactional.</p>
     */
    @Order(Integer.MAX_VALUE - 2)
    @Around("@annotation(tk.project.taskmanager.logging.aspect.annotation.MeasureExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        logger.log(String.format("Start measuring execution method %s", joinPoint.getSignature().toShortString()));
        Object result;
        long startTime = System.currentTimeMillis();

        try {
            result = joinPoint.proceed();

        } catch (Throwable throwable) {
            logger.log(String.format("Unable to measure execution time of method %s that threw exception %s",
                    joinPoint.getSignature().toShortString(), throwable.getClass().getSimpleName()));
            logger.log(String.format("Time since the start of the execution is %s millis",
                    (System.currentTimeMillis() - startTime)));
            throw throwable;
        }

        long endTime = System.currentTimeMillis();
        logger.log(String.format("Method %s was completed successfully in %s millis",
                joinPoint.getSignature().toShortString(), (endTime - startTime)));
        return result;
    }
}
