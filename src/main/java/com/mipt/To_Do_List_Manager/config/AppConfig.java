package com.mipt.To_Do_List_Manager.config;

import com.mipt.To_Do_List_Manager.repository.StubTaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфиг, демонстрирующий создание бина через @Bean.
 */
@Configuration
public class AppConfig {

    @Bean
    @Qualifier("stubTaskRepository")
    public StubTaskRepository stubTaskRepository(){
        return new StubTaskRepository();
    }
}
