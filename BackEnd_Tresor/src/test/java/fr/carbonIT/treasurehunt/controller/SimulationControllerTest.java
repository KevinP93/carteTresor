package fr.carbonIT.treasurehunt.controller;



import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class SimulationControllerTest {
    private MockMvc mockMvc;





    @Mock
    private SimulationService simulationService;

    @InjectMocks
    private SimulationController simulationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(simulationController).build();
    }

    @Test
    public void testSimulate_Success() throws Exception {
        // Given
        String filePath = "validFilePath";
        Carte carte = new Carte();
        when(simulationService.lireCarte(filePath)).thenReturn(carte);
        when(simulationService.executerSimulation(carte)).thenReturn(carte);

        // When & Then
        mockMvc.perform(get("/api/simulate")
                        .param("filePath", filePath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Simulation terminée avec succès."))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    public void testSimulate_ReadingCarteFailed() throws Exception {
        // Given
        String filePath = "invalidFilePath";
        when(simulationService.lireCarte(filePath)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/simulate")
                        .param("filePath", filePath))
                .andExpect(status().isBadRequest())  // Vérifiez que le statut est 400
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture de la carte."));
    }


    @Test
    public void testSimulate_Exception() throws Exception {
        // Given
        String filePath = "filePath";
        when(simulationService.lireCarte(filePath)).thenThrow(new RuntimeException("Simulated Exception"));

        // When & Then
        mockMvc.perform(get("/api/simulate")
                        .param("filePath", filePath))
                .andExpect(status().isInternalServerError())  // Vérifiez que le statut est 500
                .andExpect(jsonPath("$.message").value("Erreur interne du serveur : Simulated Exception"));
    }

    @Test
    public void testSimulate_IOException() throws Exception {
        // Given
        String filePath = "someFilePath";
        when(simulationService.lireCarte(filePath)).thenThrow(new IOException("Simulated IOException"));

        // When & Then
        mockMvc.perform(get("/api/simulate")
                        .param("filePath", filePath))
                .andExpect(status().isInternalServerError())  // Vérifie que le statut est 500
                .andExpect(jsonPath("$.message").value("Erreur de lecture du fichier : Simulated IOException"));
    }

    @Test
    public void testSimulate_IllegalArgumentException() throws Exception {
        // Given
        String filePath = "someFilePath";
        when(simulationService.lireCarte(filePath)).thenThrow(new IllegalArgumentException("Simulated IllegalArgumentException"));

        // When & Then
        mockMvc.perform(get("/api/simulate")
                        .param("filePath", filePath))
                .andExpect(status().isBadRequest())  // Vérifie que le statut est 400
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture de la carte : Simulated IllegalArgumentException"));
    }

    @Test
    public void testDownloadFile_Success() throws Exception {
        // Given
        String filePath = "C:\\Users\\Kevin\\Desktop\\CarbonIT\\carteTresor\\BackEnd_Tresor\\output.txt";
        File file = new File(filePath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write("Test content".getBytes());
        fos.close();

        // When & Then
        mockMvc.perform(get("/api/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.txt"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE))
                .andExpect(content().string("Test content"));

        // Clean up
        file.delete();
    }

    @Test
    public void testDownloadFile_FileNotFound() throws Exception {
        // Given
        String invalidFilePath = "C:\\invalid\\path\\output.txt";

        // When & Then
        mockMvc.perform(get("/api/download"))
                .andExpect(status().isNotFound());
    }




}
