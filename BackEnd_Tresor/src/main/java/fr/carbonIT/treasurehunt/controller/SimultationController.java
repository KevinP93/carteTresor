package fr.carbonIT.treasurehunt.controller;

import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    @PostMapping("/simulate")
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
}
