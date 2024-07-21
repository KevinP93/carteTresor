package fr.carbonIT.treasurehunt.service;

import fr.carbonIT.treasurehunt.model.Aventurier;
import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.model.Montagne;
import fr.carbonIT.treasurehunt.model.Tresor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
 class SimulationServiceTest {

    private SimulationService simulationService;
    private Carte carte;
    private Path tempFile;


    private CreationFichierService creationFichierService = mock(CreationFichierService.class);

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        simulationService = new SimulationService(creationFichierService);
        tempFile = Files.createTempFile("testCarte", ".txt");
        carte = new Carte();
        carte.setLargeur(5);
        carte.setHauteur(5);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete the temporary file
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testLireCarte() throws IOException {
        // Prepare the content for the test file
        String fileContent = "C - 5 - 4\n" +
                "M - 1 - 2\n" +
                "T - 3 - 2 - 5\n" +
                "A - Nom1 - 0 - 0 - N - A\n" +
                "A - Nom2 - 2 - 2 - E - D";

        // Write the content to the temporary file
        Files.write(tempFile, fileContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        // Execute the method to test
        Carte carte = simulationService.lireCarte(tempFile.toString());

        // Verify the results
        assertNotNull(carte);
        assertEquals(5, carte.getLargeur());
        assertEquals(4, carte.getHauteur());

        List<Montagne> montagnes = carte.getMontagnes();
        assertEquals(1, montagnes.size());
        assertEquals(1, montagnes.get(0).getX());
        assertEquals(2, montagnes.get(0).getY());

        List<Tresor> tresors = carte.getTresors();
        assertEquals(1, tresors.size());
        assertEquals(3, tresors.get(0).getX());
        assertEquals(2, tresors.get(0).getY());
        assertEquals(5, tresors.get(0).getNombre());

        List<Aventurier> aventuriers = carte.getAventuriers();
        assertEquals(2, aventuriers.size());

        Aventurier aventurier1 = aventuriers.get(0);
        assertEquals("Nom1", aventurier1.getNom());
        assertEquals(0, aventurier1.getX());
        assertEquals(0, aventurier1.getY());
        assertEquals('N', aventurier1.getOrientation());
        assertEquals("A", aventurier1.getMouvements());

        Aventurier aventurier2 = aventuriers.get(1);
        assertEquals("Nom2", aventurier2.getNom());
        assertEquals(2, aventurier2.getX());
        assertEquals(2, aventurier2.getY());
        assertEquals('E', aventurier2.getOrientation());
        assertEquals("D", aventurier2.getMouvements());
    }


    // Scénario de base : un aventurier avec des mouvements simples
    @Test
    void testExecuterSimulationAvecUnAventurier() throws IOException {
        // Préparer l'aventurier
        Aventurier aventurier = new Aventurier("Aventurier1", 2, 2, 'N', "A", 0);
        carte.setAventuriers(Collections.singletonList(aventurier));

        // Appeler la méthode de simulation
        carte = simulationService.executerSimulation(carte);

        // Vérifier la position finale de l'aventurier
        assertEquals(2, aventurier.getX(), "L'axe X de l'aventurier devrait être 2");
        assertEquals(1, aventurier.getY(), "L'axe Y de l'aventurier devrait être 1"); // Le mouvement 'A' déplace l'aventurier vers le nord
        assertEquals('N', aventurier.getOrientation(), "L'orientation de l'aventurier devrait être 'N'");
        assertEquals(0, aventurier.getTresorsCollectes(), "Le nombre de trésors collectés devrait être 0");


        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }
    // Scénario de base : un aventurier avec des mouvements de rotation vers la gauche
    @Test
    void testExecuterSimulationAvecUnAventurierGauche() throws IOException {
        // Préparer l'aventurier
        Aventurier aventurier = new Aventurier("Aventurier1", 2, 2, 'N', "G", 0);
        carte.setAventuriers(Collections.singletonList(aventurier));

        // Appeler la méthode de simulation
        carte = simulationService.executerSimulation(carte);

        // Vérifier la position finale de l'aventurier
        assertEquals(2, aventurier.getX(), "L'axe X de l'aventurier devrait être 2");
        assertEquals(2, aventurier.getY(), "L'axe Y de l'aventurier devrait être 2");
        assertEquals('O', aventurier.getOrientation(), "L'orientation de l'aventurier devrait être 'O'");
        assertEquals(0, aventurier.getTresorsCollectes(), "Le nombre de trésors collectés devrait être 0");


        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }

    // Scénario de base : un aventurier avec des mouvements de rotation vers la gauche
    @Test
    void testExecuterSimulationAvecUnAventurierDroite() throws IOException {
        // Préparer l'aventurier
        Aventurier aventurier = new Aventurier("Aventurier1", 2, 2, 'N', "D", 0);
        carte.setAventuriers(Collections.singletonList(aventurier));

        // Appeler la méthode de simulation
        carte = simulationService.executerSimulation(carte);

        // Vérifier la position finale de l'aventurier
        assertEquals(2, aventurier.getX(), "L'axe X de l'aventurier devrait être 2");
        assertEquals(2, aventurier.getY(), "L'axe Y de l'aventurier devrait être 2");
        assertEquals('E', aventurier.getOrientation(), "L'orientation de l'aventurier devrait être 'O'");
        assertEquals(0, aventurier.getTresorsCollectes(), "Le nombre de trésors collectés devrait être 0");


        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }
    // Scénario avec plusieurs aventuriers ayant des mouvements différents
    @Test
    void testExecuterSimulationAvecPlusieursAventuriers() throws IOException {
        // Préparer la carte
        carte.setLargeur(5);
        carte.setHauteur(5);
        Aventurier aventurier1 = new Aventurier("Aventurier1", 2, 2, 'N', "AA", 0);
        Aventurier aventurier2 = new Aventurier("Aventurier2", 2, 2, 'E', "A", 0);
        carte.setAventuriers(Arrays.asList(aventurier1, aventurier2));


        carte = simulationService.executerSimulation(carte);

        // Vérifier les positions finales des aventuriers
        assertEquals(2, aventurier1.getX());
        assertEquals(0, aventurier1.getY()); // Deux mouvements 'A' déplace l'aventurier vers le nord
        assertEquals(3, aventurier2.getX());
        assertEquals(2, aventurier2.getY()); // Un mouvement 'A' déplace l'aventurier vers l'est

        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));

    }


    // Scénario où les aventuriers se retrouvent sur la même case
    @Test
    void testExecuterSimulationConflitDePosition() throws IOException {
        // Préparer la carte
        carte.setLargeur(5);
        carte.setHauteur(5);
        Aventurier aventurier1 = new Aventurier("Aventurier1", 2, 2, 'N', "A", 0);
        Aventurier aventurier2 = new Aventurier("Aventurier2", 2, 1, 'S', "A", 0); // Vient de la case 2, 2
        carte.setAventuriers(Arrays.asList(aventurier1, aventurier2));

        carte = simulationService.executerSimulation(carte);

        // Les deux aventuriers ne doivent pas se retrouver sur la même case
        assertEquals(aventurier1.getX(), aventurier2.getX());
        assertNotEquals(aventurier1.getY(), aventurier2.getY());

        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }

    // Scénario avec des montagnes bloquant les déplacements
    @Test
    void testExecuterSimulationAvecMontagnes() throws IOException {
        // Préparer la carte
        carte.setLargeur(5);
        carte.setHauteur(5);
        carte.getMontagnes().add(new Montagne(2, 1)); // Une montagne bloque la case (2,1)
        Aventurier aventurier = new Aventurier("Aventurier1", 2, 2, 'N', "A", 0);
        carte.setAventuriers(Collections.singletonList(aventurier));


        carte = simulationService.executerSimulation(carte);

        // L'aventurier ne devrait pas pouvoir se déplacer vers la montagne
        assertEquals(2, aventurier.getX());
        assertEquals(2, aventurier.getY()); // Pas de changement de position

        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }

    // Scénario où un aventurier collecte des trésors
    @Test
    void testExecuterSimulationCollecteTresors() throws IOException {
        // Prépare la carte
        carte.setLargeur(5);
        carte.setHauteur(5);
        carte.getTresors().add(new Tresor(2, 1, 3)); // Trois trésors à (2,1)
        Aventurier aventurier = new Aventurier("Aventurier1", 2, 2, 'N', "A", 0);
        carte.setAventuriers(Collections.singletonList(aventurier));


        carte = simulationService.executerSimulation(carte);

        // Vérifie la collecte de trésors
        assertEquals(2, aventurier.getX());
        assertEquals(1, aventurier.getY()); // L'aventurier a bougé à (2,1)
        assertEquals(1, aventurier.getTresorsCollectes()); // Un trésor a été collecté

        verify(creationFichierService).creerFichierCarte(any(Carte.class), eq("output.txt"));
    }



    @Test
    void testTournerDroiteNord() {
        char orientationInitiale = 'N';
        char orientationAttendue = 'E';
        assertEquals(orientationAttendue, simulationService.tournerDroite(orientationInitiale));
    }

    @Test
    void testTournerDroiteEst() {
        char orientationInitiale = 'E';
        char orientationAttendue = 'S';
        assertEquals(orientationAttendue, simulationService.tournerDroite(orientationInitiale));
    }

    @Test
    void testTournerDroiteSud() {
        char orientationInitiale = 'S';
        char orientationAttendue = 'O';
        assertEquals(orientationAttendue, simulationService.tournerDroite(orientationInitiale));
    }

    @Test
    void testTournerDroiteOuest() {
        char orientationInitiale = 'O';
        char orientationAttendue = 'N';
        assertEquals(orientationAttendue, simulationService.tournerDroite(orientationInitiale));
    }

    @Test
    void testTournerDroiteOrientationInvalide() {
        char orientationInitiale = 'X'; // Orientation invalide
        char orientationAttendue = 'X'; // Doit retourner l'orientation actuelle
        assertEquals(orientationAttendue, simulationService.tournerDroite(orientationInitiale));
    }

    @Test
    void testTournerGaucheNord() {
        char orientationInitiale = 'N';
        char orientationAttendue = 'O'; // Tourner à gauche depuis le Nord donne l'Ouest
        assertEquals(orientationAttendue, simulationService.tournerGauche(orientationInitiale));
    }

    @Test
    void testTournerGaucheOuest() {
        char orientationInitiale = 'O';
        char orientationAttendue = 'S'; // Tourner à gauche depuis l'Ouest donne le Sud
        assertEquals(orientationAttendue, simulationService.tournerGauche(orientationInitiale));
    }

    @Test
    void testTournerGaucheSud() {
        char orientationInitiale = 'S';
        char orientationAttendue = 'E'; // Tourner à gauche depuis le Sud donne l'Est
        assertEquals(orientationAttendue, simulationService.tournerGauche(orientationInitiale));
    }

    @Test
    void testTournerGaucheEst() {
        char orientationInitiale = 'E';
        char orientationAttendue = 'N'; // Tourner à gauche depuis l'Est donne le Nord
        assertEquals(orientationAttendue, simulationService.tournerGauche(orientationInitiale));
    }

    @Test
    void testTournerGaucheOrientationInvalide() {
        char orientationInitiale = 'X'; // Orientation invalide
        char orientationAttendue = 'X'; // Doit retourner l'orientation actuelle
        assertEquals(orientationAttendue, simulationService.tournerGauche(orientationInitiale));
    }

}
