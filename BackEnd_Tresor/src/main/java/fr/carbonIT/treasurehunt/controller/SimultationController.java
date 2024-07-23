package fr.carbonIT.treasurehunt.controller;

import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimultationController {

    @Autowired
    private SimulationService simulationService;

    /**
     * Gère la simulation des mouvements des aventuriers en fonction du fichier spécifié.
     * <p>
     * Cette méthode est exposée en tant que point de terminaison POST de l'API. Elle prend le chemin du fichier comme paramètre de requête,
     * lit la carte depuis le fichier, exécute la simulation des mouvements des aventuriers sur cette carte, et renvoie le résultat de la simulation.
     * Le résultat contient un message de statut ainsi que la représentation sous forme de chaîne de la carte après la simulation.
     * </p>
     *
     * @param filePath Le chemin d'accès au fichier contenant les informations de la carte.
     * @return Une {@link Map} contenant le message de statut et le résultat de la simulation.
     *         - {@code "message"} : Un message indiquant le succès ou l'échec de la simulation.
     *         - {@code "result"} : La représentation sous forme de chaîne de la carte après la simulation, ou un message d'erreur en cas de problème.
     */
    @GetMapping("/simulate")
    public Map<String, String> simulate(@RequestParam String filePath) {
        Map<String, String> response = new HashMap<>();
        try {
            Carte carte = simulationService.lireCarte(filePath);
            if (carte != null) {
                carte = simulationService.executerSimulation(carte);
                response.put("message", "Simulation terminée avec succès.");
                response.put("result", carte.toString());
            } else {
                response.put("message", "Erreur lors de la lecture de la carte.");
            }
        } catch (Exception e) {
            response.put("message", "Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return response;
    }

    /**
     * Endpoint pour télécharger le fichier de sortie généré par la simulation.
     *
     * @return ResponseEntity contenant le fichier en tant que ressource d'entrée.
     * @throws IOException Si une erreur survient lors de la lecture du fichier.
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile() throws IOException {
        File file = new File("C:\\Users\\Kevin\\Desktop\\CarbonIT\\carteTresor\\BackEnd_Tresor\\output.txt");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        FileInputStream fis = new FileInputStream(file);
        InputStreamResource resource = new InputStreamResource(fis);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.txt");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

}
