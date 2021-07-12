package com.example.myapplication.classi;

import java.util.Comparator;

public class AnnuncioPrezzoComparator implements Comparator<Annuncio> {
    @Override
    public int compare(Annuncio a1, Annuncio a2) {
        if(a1.getPrezzoMensile()>a2.getPrezzoMensile())
            return 1;
        else if(a1.getPrezzoMensile()<a2.getPrezzoMensile())
            return -1;

        return 0;
    }
}
