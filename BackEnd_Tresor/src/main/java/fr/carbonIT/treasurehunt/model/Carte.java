package fr.carbonIT.treasurehunt.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Carte {

    private int largeur;
    private int hauteur;
    private List<Montagne> montagnes;
    private List<Tresor> tresors;
    private List<Aventurier> aventuriers;

    public Carte() {
        this.largeur = 0;
        this.hauteur = 0;
        this.montagnes = new ArrayList<>();
        this.tresors = new ArrayList<>();
        this.aventuriers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Carte{" +
                "largeur=" + largeur +
                ", hauteur=" + hauteur +
                ", montagnes=" + montagnes +
                ", tresors=" + tresors +
                ", aventuriers=" + aventuriers +
                '}';
    }

}
