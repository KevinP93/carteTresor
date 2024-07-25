package fr.carbonIT.treasurehunt.controller;

import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.CreationFichierService;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class SimulationController {

    @Autowired
    private SimulationService simulationService;
    private static final Logger logger = LoggerFactory.getLogger(CreationFichierService.class);
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


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
    public ResponseEntity<Map<String, Object>> simulate(@RequestParam String filePath) {
        Map<String, Object> response = new HashMap<>();
        try {
            Carte carte = simulationService.lireCarte(filePath);
            if (carte != null) {
                carte = simulationService.executerSimulation(carte);
                response.put("message", "Simulation terminée avec succès.");
                response.put("result", carte); // Envoyer l'objet carte directement
            } else {
                response.put("message", "Erreur lors de la lecture de la carte.");
            }
        } catch (Exception e) {
            response.put("message", "Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return ResponseEntity.ok(response);
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

        FileInputStream fis = null;
        InputStreamResource resource = null;
        try {
            fis = new FileInputStream(file);
            resource = new InputStreamResource(fis);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.txt");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);

            // Schedule the file deletion after the response is sent
            scheduleFileDeletion(file);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions (e.g., logging)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Planifie la suppression d'un fichier après un certain délai.
     *
     * @param file Le fichier à supprimer.
     */
    void scheduleFileDeletion(File file) {
        executor.schedule(() -> {
            try {
                if (file.exists()) {
                    Files.delete(file.toPath());
                    logger.info("Fichier supprimé avec succès : {}");
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception (e.g., logging)
            }
        }, 10, TimeUnit.SECONDS);
    }

}
