package fr.carbonIT.treasurehunt.service;

import fr.carbonIT.treasurehunt.model.Aventurier;
import fr.carbonIT.treasurehunt.model.Carte;
import fr.carbonIT.treasurehunt.model.Montagne;
import fr.carbonIT.treasurehunt.model.Tresor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SimulationService {

    @Autowired
    private CreationFichierService creationFichierService;


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
     *
     * La méthode gère le déplacement des aventuriers en fonction de leurs mouvements
     * spécifiés. Elle continue la simulation jusqu'à ce que tous les mouvements aient
     * été traités. Une fois la simulation terminée, elle crée un fichier de sortie
     * contenant l'état final de la carte.
     *
     * @param carte La carte contenant les aventuriers et les autres éléments du jeu.
     * @return La carte mise à jour après l'exécution de la simulation.
     */
    public Carte executerSimulation(Carte carte) {
        List<Aventurier> aventuriers = new ArrayList<>(carte.getAventuriers());
        int nombreAventuriers = aventuriers.size();

        boolean simulationContinue = true;

        while (simulationContinue) {
            simulationContinue = false;
            for (int i = 0; i < nombreAventuriers; i++) {
                Aventurier aventurier = aventuriers.get(i);

                if (aventurier.getMouvements() != null && !aventurier.getMouvements().isEmpty()) {
                    char mouvement = aventurier.getMouvements().charAt(0); // Obtenir le prochain mouvement
                    aventurier.setMouvements(aventurier.getMouvements().substring(1)); // Enlever le mouvement traité

                    switch (mouvement) {
                        case 'A':
                            deplacerAventurier(aventurier, carte);
                            break;
                        case 'D':
                            aventurier.setOrientation(tournerDroite(aventurier.getOrientation()));
                            break;
                        case 'G':
                            aventurier.setOrientation(tournerGauche(aventurier.getOrientation()));
                            break;
                    }

                    // Vérifiez si l'aventurier a encore des mouvements à faire
                    if (!aventurier.getMouvements().isEmpty()) {
                        simulationContinue = true; // Continuer la simulation si des mouvements restent
                    }
                }
            }
        }

        // Affichage du résultat
        for (Aventurier aventurier : carte.getAventuriers()) {
            System.out.println(aventurier.getNom() + " a trouvé : " + aventurier.getTresorsCollectes() + " trésor(s)");
        }

        // Crée le fichier de sortie après la simulation
        try {
            creationFichierService.creerFichierCarte(carte, "output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carte;
    }


    /**
     * Déplace un aventurier sur la carte en fonction de son orientation actuelle.
     *
     * La méthode calcule la nouvelle position de l'aventurier en fonction de son orientation,
     * vérifie si cette position est valide et libre, puis déplace l'aventurier à cette position
     * si toutes les conditions sont remplies. Si l'aventurier se déplace sur une case contenant
     * un trésor, il le collecte. Si la position n'est pas valide ou est occupée, le déplacement est annulé.
     *
     * @param aventurier L'aventurier à déplacer.
     * @param carte La carte sur laquelle se trouve l'aventurier.
     */
    private void deplacerAventurier(Aventurier aventurier, Carte carte) {
        // Sauvegarde la position actuelle
        int xInitial = aventurier.getX();
        int yInitial = aventurier.getY();

        // Calcule la nouvelle position en fonction de l'orientation
        int nouvelleX = xInitial;
        int nouvelleY = yInitial;

        switch (aventurier.getOrientation()) {
            case 'N':
                nouvelleY -= 1;
                break;
            case 'S':
                nouvelleY += 1;
                break;
            case 'E':
                nouvelleX += 1;
                break;
            case 'O':
                nouvelleX -= 1;
                break;
        }

        // Vérifie si la nouvelle position est valide et libre
        if (verifierPositionValide(nouvelleX, nouvelleY, aventurier, carte)) {
            // Déplace l'aventurier à la nouvelle position
            aventurier.setX(nouvelleX);
            aventurier.setY(nouvelleY);

            // Collecte un trésor si possible
            collecterTresor(aventurier, carte);
        } else {
            // Annule le déplacement si la position n'est pas valide ou est occupée
            System.out.println(aventurier.getNom() + " ne peut pas se déplacer vers (" + nouvelleX + "," + nouvelleY + ").");
        }
    }

    /**
     * Vérifie si une position donnée est valide pour le déplacement d'un aventurier.
     *
     * La méthode vérifie plusieurs conditions pour déterminer si une position est
     * valide pour le déplacement d'un aventurier. Elle vérifie si la position est
     * dans les limites de la carte, si elle n'est pas occupée par une montagne et si
     * elle n'est pas déjà occupée par un autre aventurier.
     *
     * @param x La coordonnée X de la position à vérifier.
     * @param y La coordonnée Y de la position à vérifier.
     * @param aventurierCourant L'aventurier qui tente de se déplacer. Ce paramètre
     *                           est utilisé pour ignorer cet aventurier lors de la
     *                           vérification de la présence d'autres aventuriers à la
     *                           même position.
     * @param carte La carte sur laquelle se fait la vérification. Contient les informations
     *              sur les montagnes et les autres aventuriers présents.
     * @return true si la position est valide et libre, false sinon.
     */
    private boolean verifierPositionValide(int x, int y, Aventurier aventurierCourant, Carte carte) {
        // Vérifie les limites de la carte
        if (x < 0 || x >= carte.getLargeur() || y < 0 || y >= carte.getHauteur()) {
            return false;
        }

        // Vérifie le nbr de montagnes
        for (Montagne montagne : carte.getMontagnes()) {
            if (montagne.getX() == x && montagne.getY() == y) {
                System.out.println("Position (" + x + "," + y + ") est bloquée par une montagne.");
                return false;
            }
        }

        // Vérifie la présence d'autres aventuriers
        boolean positionOccupee = carte.getAventuriers().stream()
                .anyMatch(a -> !a.equals(aventurierCourant) && a.getX() == x && a.getY() == y);

        if (positionOccupee) {
            System.out.println("Position (" + x + "," + y + ") est déjà occupée par un autre aventurier.");
        }

        return !positionOccupee;
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
    char tournerDroite(char orientation) {
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
    char tournerGauche(char orientation) {
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

    public SimulationService(CreationFichierService creationFichierService) {
        this.creationFichierService = creationFichierService;
    }


}
