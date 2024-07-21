package fr.carbonIT.treasurehunt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import fr.carbonIT.treasurehunt.model.Aventurier;
import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.model.Montagne;
import fr.carbonIT.treasurehunt.model.Tresor;
import fr.carbonIT.treasurehunt.service.CreationFichierService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;



class CreationFichierServiceTest {

    private CreationFichierService creationFichierService;
    private Carte carte;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        creationFichierService = new CreationFichierService();
        tempFile = Files.createTempFile("testCarte", ".txt");
        carte = new Carte();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testCreerFichierCarteAvecMontagnes() throws IOException {
        carte.setHauteur(5);
        carte.setLargeur(5);
        carte.setMontagnes(Collections.singletonList(new Montagne(1, 1)));
        carte.setTresors(Collections.emptyList());
        carte.setAventuriers(Collections.emptyList());

        creationFichierService.creerFichierCarte(carte, tempFile.toString());

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".M...");
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
        }
    }

    @Test
    void testCreerFichierCarteAvecMontagnesEtTresors() throws IOException {
        carte.setHauteur(5);
        carte.setLargeur(5);
        carte.setMontagnes(Collections.singletonList(new Montagne(1, 1)));
        carte.setTresors(Arrays.asList(
                new Tresor(2, 2, 5),
                new Tresor(3, 3, 3),
                new Tresor(2, 2, 2) // Trésor supplémentaire au même endroit
        ));
        carte.setAventuriers(Collections.emptyList());

        creationFichierService.creerFichierCarte(carte, tempFile.toString());

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".M...");
            assertThat(reader.readLine()).isEqualTo("..T(7)..");
            assertThat(reader.readLine()).isEqualTo("...T(3).");
            assertThat(reader.readLine()).isEqualTo(".....");
        }
    }

    @Test
    void testCreerFichierCarteAvecMontagnesTresorsEtAventuriers() throws IOException {
        carte.setHauteur(5);
        carte.setLargeur(5);
        carte.setMontagnes(Arrays.asList(new Montagne(1, 1), new Montagne(3, 3)));
        carte.setTresors(Arrays.asList(
                new Tresor(2, 2, 3),
                new Tresor(2, 4, 2)
        ));
        carte.setAventuriers(Arrays.asList(
                new Aventurier("Aventurier1", 2, 3, 'N', "A", 0),
                new Aventurier("Aventurier2", 4, 4, 'S', "A", 0)
        ));

        creationFichierService.creerFichierCarte(carte, tempFile.toString());

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".M...");
            assertThat(reader.readLine()).isEqualTo("..T(3)..");
            assertThat(reader.readLine()).isEqualTo("..AM.");
            assertThat(reader.readLine()).isEqualTo("..T(2).A");
        }
    }

    @Test
    void testCreerFichierCarteVide() throws IOException {
        carte.setHauteur(5);
        carte.setLargeur(5);
        carte.setMontagnes(Collections.emptyList());
        carte.setTresors(Collections.emptyList());
        carte.setAventuriers(Collections.emptyList());

        creationFichierService.creerFichierCarte(carte, tempFile.toString());

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toFile()))) {
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
            assertThat(reader.readLine()).isEqualTo(".....");
        }
    }
}
