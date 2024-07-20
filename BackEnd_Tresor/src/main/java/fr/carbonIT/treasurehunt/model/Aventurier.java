package fr.carbonIT.treasurehunt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Aventurier {

    private String nom;
    private int x;
    private int y;
    private char orientation;
    private String mouvements;
    private int tresorsCollectes;

    public Aventurier(String nom, int xAventurier, int yAventurier, char orientation, String mouvements, int tresorsCollectes) {
        this.nom = nom;
        this.x = xAventurier;
        this.y = yAventurier;
        this.orientation = orientation;
        this.mouvements = mouvements;
        this.tresorsCollectes = tresorsCollectes;
    }

    @Override
    public String toString() {
        return "Aventurier{" +
                "nom=" + nom +
                ", x=" + x +
                ", y=" + y +
                ", orientation=" + orientation +
                ", mouvements=" + mouvements +
                '}';
    }
}
