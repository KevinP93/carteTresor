package fr.carbonIT.treasurehunt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"fr.carbonIT.treasurehunt"})
@EntityScan("fr.carbonIT.treasurehunt.model")
public class TreasureHuntApplication {

    public static void main(String[] args){
        SpringApplication.run(TreasureHuntApplication.class, args);
    }
}
