package fr.carbonIT.treasurehunt;

import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
 class TreasureHuntApplicationTests {

    @Autowired
    private SimulationService simulationService;

    @Test
    void testLireCarte() throws IOException {
        Carte carte = simulationService.lireCarte("src/main/resources/data/input.txt");
        assertNotNull(carte);
        assertEquals(3, carte.getLargeur());
        assertEquals(4, carte.getHauteur());
    }
}
