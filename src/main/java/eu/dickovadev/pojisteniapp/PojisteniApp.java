package eu.dickovadev.pojisteniapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "eu.dickovadev.pojisteniapp")
@EnableJpaRepositories
public class PojisteniApp {

    public static void main(String[] args) {
        SpringApplication.run(PojisteniApp.class, args);
    }
}
