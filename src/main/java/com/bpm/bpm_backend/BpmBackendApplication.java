package com.bpm.bpm_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // <--- ¡ESTA ES LA ETIQUETA QUE REVIVE AL SERVIDOR!
public class BpmBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BpmBackendApplication.class, args);
    }

}