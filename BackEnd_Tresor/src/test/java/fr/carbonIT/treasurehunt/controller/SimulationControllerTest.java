package fr.carbonIT.treasurehunt.controller;



import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



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
                .andExpect(status().isOk())
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture du fichier : Simulated Exception"));
    }



}
