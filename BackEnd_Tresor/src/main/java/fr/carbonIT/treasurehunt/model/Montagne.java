package fr.carbonIT.treasurehunt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Montagne {

    private int x;
    private int y;


    public Montagne(int xMontagne, int yMontagne) {
        this.x = xMontagne;
        this.y = yMontagne;
    }

    @Override
    public String toString() {
        return "Montagne{" +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
