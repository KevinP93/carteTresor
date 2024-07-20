package fr.carbonIT.treasurehunt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tresor {

    private int x;
    private int y;
    private int nombre;

    public Tresor(int xTresor, int yTresor, int nombre) {
        this.x = xTresor;
        this.y = yTresor;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Tresor{" +
                ", x=" + x +
                ", y=" + y +
                ", nombre=" + nombre +
                '}';
    }
}
