package api.ytter.backend.logger;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggerAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggerAspect.class);

    @Before("execution(public * api.ytter.backend.controller..*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String clientIp = request.getRemoteAddr();
            String requestUri = request.getRequestURI();
            String method = request.getMethod();
            logger.info("Executing: " + joinPoint.getSignature().getName());
            logger.info("Client IP: " + clientIp);
            logger.info("Request URI: " + requestUri);
            logger.info("HTTP Method: " + method);
        }
    }
}
