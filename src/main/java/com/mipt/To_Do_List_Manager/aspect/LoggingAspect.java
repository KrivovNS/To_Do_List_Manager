package com.mipt.To_Do_List_Manager.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования начала и завершения выполнения методов сервисного слоя(путь:
 * com.mipt.To_Do_List_Manager.service.TaskService.java).
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Advice для создания логов до выполнения метода и после.
     * Фиксируем результат выполнения или пробрасываемые исключения.
     * @param joinPoint - joinpoint к перехватываемому методу
     * @return - результат выполнения метода
     * @throws Throwable - exception в случае проброса исключения метода
     */
    @Around("execution(* com.mipt.To_Do_List_Manager.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getClass().getSimpleName();

        log.info("STARTED method: {}.{}", className, methodName);
        try {
            Object methodResult = joinPoint.proceed();
            if (methodResult != null) {
                log.info("ENDED method: {}.{}; result: {}", className, methodName, methodResult);
            } else {
                log.info("ENDED method: {}.{}; result: null", className, methodName);
            }

            return methodResult;
        } catch (Throwable e) {
            log.info("ENDED method: {}.{}; exception: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

}
