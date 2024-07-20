package fr.carbonIT.treasurehunt.service;

import fr.carbonIT.treasurehunt.model.Aventurier;
import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.model.Montagne;
import fr.carbonIT.treasurehunt.model.Tresor;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SimulationService {

    /**
     * Lit un fichier et crée une carte en fonction de son contenu.
     * <p>
     * Cette méthode lit le fichier spécifié par le chemin d'accès, extrait les informations sur la carte,
     * les montagnes, les trésors et les aventuriers, puis les ajoute à un objet {@link Carte}.
     * Le format attendu du fichier est défini pour chaque type d'entité (carte, montagne, trésor, aventurier).
     * </p>
     *
     * @param filePath Le chemin d'accès au fichier à lire.
     * @return L'objet {@link Carte} contenant les informations lues depuis le fichier.
     * @throws IOException Si un problème survient lors de la lecture du fichier.
     */
    public Carte lireCarte(String filePath) throws IOException {

        // Création de la carte avec des listes initialisées
        Carte carte = new Carte();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("C")) {
                    String[] parts = line.split(" - ");
                    carte.setLargeur(Integer.parseInt(parts[1]));
                    carte.setHauteur(Integer.parseInt(parts[2]));
                } else if (line.startsWith("M")) {
                    String[] parts = line.split(" - ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    carte.getMontagnes().add(new Montagne(x, y));
                } else if (line.startsWith("T")) {
                    String[] parts = line.split(" - ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int nombre = Integer.parseInt(parts[3]);
                    carte.getTresors().add(new Tresor(x, y, nombre));
                } else if (line.startsWith("A")) {
                    String[] parts = line.split(" - ");
                    String nom = parts[1];
                    int x = Integer.parseInt(parts[2]);
                    int y = Integer.parseInt(parts[3]);
                    char orientation = parts[4].charAt(0);
                    String mouvements = parts[5];
                    carte.getAventuriers().add(new Aventurier(nom, x, y, orientation, mouvements,0));
                }
            }
        }
        return carte;
    }

    /**
     * Exécute la simulation des mouvements des aventuriers sur la carte.
     * <p>
     * Cette méthode parcourt les aventuriers, traite leurs mouvements selon les instructions, et gère les
     * collisions et interactions avec les trésors et montagnes. Les mouvements sont traités un par un
     * pour chaque aventurier et les conflits de position sont vérifiés.
     * </p>
     *
     * @param carte L'objet {@link Carte} contenant les informations sur la carte, les montagnes, les trésors, et les aventuriers.
     * @return L'objet {@link Carte} après avoir exécuté la simulation.
     */
    public Carte executerSimulation(Carte carte) {

        // Utilisation d'un Set pour vérifier les conflits d'aventuriers
        Set<String> positionsAventuriers = new HashSet<>();

        for (Aventurier aventurier : carte.getAventuriers()) {
            if (aventurier.getMouvements() != null) {
                if (!processerMouvements(aventurier, carte, positionsAventuriers)) {
                    return carte; // Sortir de la méthode en cas de conflit
                }
            }
            System.out.println(aventurier.getNom() + " a trouvé : " + aventurier.getTresorsCollectes() + " trésor(s)");
        }


        return carte;
    }


    /**
     * Traite les mouvements d'un aventurier sur la carte.
     * <p>
     * Cette méthode applique les mouvements spécifiés pour un aventurier, met à jour sa position en fonction de son orientation,
     * et vérifie les collisions avec les montagnes. Les mouvements sont traités un par un et la méthode renvoie un indicateur
     * de conflit si deux aventuriers se retrouvent sur la même case.
     * </p>
     *
     * @param aventurier L'aventurier dont les mouvements doivent être traités.
     * @param carte L'objet {@link Carte} contenant les informations nécessaires pour le déplacement et les collisions.
     * @param positionsAventuriers Un ensemble contenant les positions des aventuriers pour détecter les conflits de position.
     * @return {@code true} si les mouvements ont été traités sans conflit ; {@code false} en cas de conflit de position.
     */
    private boolean processerMouvements(Aventurier aventurier, Carte carte, Set<String> positionsAventuriers) {
        //TODO: dabord verifier s'il y a plsr aventurier sur la carte
        for (char mouvement : aventurier.getMouvements().toCharArray()) {
            switch (mouvement) {
                case 'A':
                    deplacerAventurier(aventurier,carte);
                    break;
                case 'D':
                    aventurier.setOrientation(tournerDroite(aventurier.getOrientation()));
                    break;
                case 'G':
                    aventurier.setOrientation(tournerGauche(aventurier.getOrientation()));
                    break;
            }

            /*
            // Vérifier les conflits d'aventuriers
            String positionKey = aventurier.getX() + "," + aventurier.getY();
            if (positionsAventuriers.contains(positionKey)) {
                System.out.println("Erreur : Deux aventuriers sont sur la même case (" + positionKey + ") !");
                return false; // Signaler un conflit
            }
            positionsAventuriers.add(positionKey);*/



            // Vérifier si l'aventurier rencontre une montagne
            for (Montagne montagne : carte.getMontagnes()) {
                if (aventurier.getX() == montagne.getX() && aventurier.getY() == montagne.getY()) {
                    System.out.println(aventurier.getNom() + " est arrêté par une montagne.");
                    // TODO : Ajoutez une logique ici
                }
            }
        }
        return true; // Aucun conflit trouvé
    }


    /**
     * Déplace un aventurier en fonction de son orientation actuelle et collecte un trésor s'il est présent sur la case.
     * <p>
     * Cette méthode met à jour la position de l'aventurier en fonction de son orientation actuelle
     * et appelle la méthode {@link #collecterTresor(Aventurier, Carte)} pour gérer la collecte des trésors
     * sur la case actuelle.
     * </p>
     *
     * @param aventurier L'aventurier à déplacer.
     * @param carte L'objet {@link Carte} contenant les informations nécessaires pour le déplacement et la collecte des trésors.
     */
    private void deplacerAventurier(Aventurier aventurier, Carte carte) {
        switch (aventurier.getOrientation()) {
            case 'N':
                aventurier.setY(aventurier.getY() - 1);
                break;
            case 'S':
                aventurier.setY(aventurier.getY() + 1);
                break;
            case 'E':
                aventurier.setX(aventurier.getX() + 1);
                break;
            case 'O':
                aventurier.setX(aventurier.getX() - 1);
                break;
        }
        collecterTresor(aventurier, carte);
    }

    /**
     * Gère l'orientation de l'aventurier vers la droite.
     * <p>
     * Cette méthode modifie l'orientation de l'aventurier en fonction de sa direction actuelle.
     * Par exemple, si l'aventurier est orienté vers le Nord ('N'), il sera orienté vers l'Est ('E') après un tour à droite.
     * </p>
     *
     * @param orientation La direction actuelle de l'aventurier. Peut être 'N' (Nord), 'E' (Est), 'S' (Sud) ou 'O' (Ouest).
     * @return La nouvelle orientation de l'aventurier après avoir tourné à droite. Retourne l'orientation actuelle si elle est invalide.
     */
    private char tournerDroite(char orientation) {
        switch (orientation) {
            case 'N':
                return 'E';
            case 'E':
                return 'S';
            case 'S':
                return 'O';
            case 'O':
                return 'N';
            default:
                return orientation; // Retourne l'orientation actuelle si elle est invalide
        }
    }

    /**
     * Tourne l'orientation de l'aventurier vers la gauche.
     * <p>
     * Cette méthode modifie l'orientation de l'aventurier en fonction de sa direction actuelle.
     * Par exemple, si l'aventurier est orienté vers le Nord ('N'), il sera orienté vers l'Ouest ('O') après un tour à gauche.
     * </p>
     *
     * @param orientation La direction actuelle de l'aventurier. Peut être 'N' (Nord), 'E' (Est), 'S' (Sud) ou 'O' (Ouest).
     * @return La nouvelle orientation de l'aventurier après avoir tourné à gauche. Retourne l'orientation actuelle si elle est invalide.
     */
    private char tournerGauche(char orientation) {
        switch (orientation) {
            case 'N':
                return 'O';
            case 'O':
                return 'S';
            case 'S':
                return 'E';
            case 'E':
                return 'N';
            default:
                return orientation; // Retourne l'orientation actuelle si elle est invalide
        }
    }


    /**
     * Permet à un aventurier de ramasser un trésor s'il se trouve sur une case contenant un trésor.
     * <p>
     * Cette méthode vérifie si l'aventurier se trouve sur une case contenant un ou plusieurs trésors.
     * Si des trésors sont présents, l'aventurier ramasse un seul trésor et la méthode quitte immédiatement.
     * La quantité de trésors sur la case est décrémentée et le nombre total de trésors collectés par l'aventurier est mis à jour.
     * </p>
     *
     * @param aventurier L'aventurier qui essaie de ramasser un trésor. Il doit avoir des coordonnées définies.
     * @param carte La carte sur laquelle se trouvent les trésors. Elle est utilisée pour vérifier la présence de trésors sur la case actuelle de l'aventurier.
     */
    private void collecterTresor(Aventurier aventurier, Carte carte) {
        // Vérifier les trésors sur la case actuelle de l'aventurier
        List<Tresor> tresorsSurCase = carte.getTresors().stream()
                .filter(t -> t.getX() == aventurier.getX() && t.getY() == aventurier.getY() && t.getNombre() > 0)
                .collect(Collectors.toList());

        if (!tresorsSurCase.isEmpty()) {
            for (Tresor tresor : tresorsSurCase) {
                if (tresor.getNombre() > 0) {
                    // Ramasser un trésor
                    tresor.setNombre(tresor.getNombre() - 1);
                    aventurier.setTresorsCollectes(aventurier.getTresorsCollectes()+1);
                    System.out.println(aventurier.getNom() + " a trouvé un trésor. Nombre restant : " + tresor.getNombre());
                    return; // Quitter après avoir ramassé un trésor
                }
            }
        }
    }

}
