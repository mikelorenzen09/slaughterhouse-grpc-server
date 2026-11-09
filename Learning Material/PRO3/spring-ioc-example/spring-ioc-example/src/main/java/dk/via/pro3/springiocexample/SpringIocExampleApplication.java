package dk.via.pro3.springiocexample;

import dk.via.pro3.springiocexample.model.Drink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringIocExampleApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext applicationContext = SpringApplication.run(
                SpringIocExampleApplication.class, args)) {
            Drink drinkBean = applicationContext.getBean(Drink.class);
            drinkBean.confirmObjectCreation();
        }
    }

}
