package fr.carbonIT.treasurehunt.controller;

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

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Le fichier est vide.");
            }

            // Définir le chemin du fichier
            Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            response.put("message", "Fichier téléchargé avec succès.");
            response.put("filePath", filePath.toString());

        } catch (IOException e) {
            response.put("message", "Erreur lors du téléversement du fichier : " + e.getMessage());
        }
        return response;
    }
}
