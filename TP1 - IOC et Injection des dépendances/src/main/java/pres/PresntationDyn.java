package pres;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Scanner;

public class PresntationDyn {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner( new File("config.txt"));
        String daoClassName = scanner.nextLine();
        String metierClassName = scanner.nextLine();
        Class cDao = Class.forName( daoClassName );
        Class cMetier = Class.forName( metierClassName );

        IDao dao = (IDao) cDao.newInstance();
        IMetier metier = (IMetier) cMetier.newInstance();

        Method method = metier.getClass().getMethod("setDao", IDao.class);

        method.invoke( metier, dao );

        System.out.println( "res : "+ metier.calcul() );

    }
}
