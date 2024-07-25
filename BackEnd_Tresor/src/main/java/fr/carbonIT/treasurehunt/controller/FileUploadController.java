package fr.carbonIT.treasurehunt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads";

    static {
        // Création du répertoire si il n'existe pas
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Gère le téléversement d'un fichier via une requête HTTP POST.
     *
     * @param file Le fichier à téléverser, fourni en tant que paramètre de requête.
     * @return Une réponse HTTP contenant un message de statut et, éventuellement, le chemin du fichier sauvegardé.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("message", "Le fichier est vide.");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérifier l'extension du fichier
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".txt")) {
                response.put("message", "Le fichier doit être au format .txt.");
                return ResponseEntity.badRequest().body(response);
            }

            // Définir le chemin du fichier
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            response.put("message", "Fichier téléchargé avec succès.");
            response.put("filePath", filePath.toString());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("message", "Erreur lors du téléversement du fichier : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
