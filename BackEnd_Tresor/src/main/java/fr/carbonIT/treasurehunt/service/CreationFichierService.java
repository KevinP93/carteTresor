package fr.carbonIT.treasurehunt.service;

import fr.carbonIT.treasurehunt.model.Aventurier;
import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.model.Montagne;
import fr.carbonIT.treasurehunt.model.Tresor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class CreationFichierService {

    private static final Logger logger = LoggerFactory.getLogger(CreationFichierService.class);

    /**
     * Crée un fichier représentant une carte de jeu basée sur les informations fournies dans l'objet Carte.
     *
     * La méthode génère une carte sous forme de fichier texte où les montagnes, trésors, et aventuriers
     * sont représentés par des symboles spécifiques. Les montagnes sont représentées par 'M', les trésors
     * par 'T(nombre)' où 'nombre' est le nombre de trésors à cette position, et les aventuriers par 'A'.
     * Les positions vides sont représentées par des points ('.'). La méthode écrit cette carte dans le fichier
     * spécifié par le chemin donné.
     *
     * @param carte L'objet Carte contenant les informations sur les montagnes, trésors, et aventuriers à
     *              inclure dans le fichier.
     * @param filePath Le chemin du fichier où la carte sera créée. Ce chemin est utilisé pour ouvrir
     *                 un flux de sortie et écrire les données de la carte dans le fichier.
     * @throws IOException Si une erreur se produit lors de la création ou de l'écriture dans le fichier.
     */
    public void creerFichierCarte(Carte carte, String filePath) throws IOException {
        logger.info("Début de la création du fichier : {}", filePath);
        String absolutePath = new File(filePath).getAbsolutePath();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int hauteur = carte.getHauteur();
            int largeur = carte.getLargeur();

            // Créer une carte vide avec des points (.)
            char[][] map = new char[hauteur][largeur];
            for (int i = 0; i < hauteur; i++) {
                for (int j = 0; j < largeur; j++) {
                    map[i][j] = '.';
                }
            }

            // Place les montagnes sur la carte
            for (Montagne montagne : carte.getMontagnes()) {
                map[montagne.getY()][montagne.getX()] = 'M';
            }

            // Compte les trésors par position
            Map<String, Integer> tresorsMap = new HashMap<>();
            for (Tresor tresor : carte.getTresors()) {
                String key = tresor.getX() + "," + tresor.getY();
                tresorsMap.put(key, tresorsMap.getOrDefault(key, 0) + tresor.getNombre());
            }

            // Place les aventuriers sur la carte
            for (Aventurier aventurier : carte.getAventuriers()) {
                map[aventurier.getY()][aventurier.getX()] = 'A';
            }

            // Écrit la carte dans le fichier
            for (int i = 0; i < hauteur; i++) {
                for (int j = 0; j < largeur; j++) {
                    String key = j + "," + i;
                    if (map[i][j] == '.') {
                        // Vérifie s'il y a des trésors à cette position
                        int count = tresorsMap.getOrDefault(key, 0);
                        if (count > 0) {
                            writer.write("T(" + count + ")");
                        } else {
                            writer.write(map[i][j]);
                        }
                    } else {
                        writer.write(map[i][j]);
                    }
                }
                writer.newLine();
            }

            logger.info("Fichier créé avec succès : {}", filePath);
            System.out.println("Fichier créé avec succès à : " + absolutePath);
        } catch (IOException e) {
            logger.error("Erreur lors de la création du fichier : {}", filePath, e);
            throw e;
        }
    }
}
