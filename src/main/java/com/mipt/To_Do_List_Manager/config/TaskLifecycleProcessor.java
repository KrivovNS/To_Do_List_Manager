package com.mipt.To_Do_List_Manager.config;

import com.mipt.To_Do_List_Manager.repository.TaskRepository;
import com.mipt.To_Do_List_Manager.service.TaskService;
import io.micrometer.common.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Класс, логирующий этапы инициализации и создания бинов TaskRepository и TaskService.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) {
        if ((bean instanceof TaskRepository) || (bean instanceof TaskService)) {
            log.info("BEFORE initialization: beanName='{}', type='{}'", beanName,
                bean.getClass().getName());
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
        if ((bean instanceof TaskRepository) || (bean instanceof TaskService)) {
            log.info("AFTER initialization: beanName='{}', type='{}'", beanName,
                bean.getClass().getName());
        }

        return bean;
    }
}
