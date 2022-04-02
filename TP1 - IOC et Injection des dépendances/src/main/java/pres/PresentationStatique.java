package pres;

import dao.DaoImpl;
import metier.MetierImpl;

public class PresentationStatique {
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao);
//        metier.setDao(dao);
        System.out.println( "res : " + metier.calcul() );
    }
}
