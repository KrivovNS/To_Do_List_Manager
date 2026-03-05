package com.mipt.To_Do_List_Manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ToDoListManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToDoListManagerApplication.class, args);
	}

}
