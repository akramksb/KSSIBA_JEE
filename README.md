# KSSIBA_JEE
*Cette repo est une collection des traveaux pratiques JEE - ENSET 2021/2022*

## [1 - Inversion de contrôle et Injection des dépendances](./TP1%20-%20IOC%20et%20Injection%20des%20d%C3%A9pendances/)


#### l'interface `IDao`

```java
public interface IDao {
    double getValue();
}
```

#### Implémentation de l'interface

```java
public class DaoImpl implements IDao{
    @Override
    public double getValue() {
        return 5;
    }
}
```

#### l'interface `IMetier`

```java
public interface IMetier {
    double calcul();
}
```

#### Implémentation de l'interface IMetier avec `couplage faible`  
*c'est à dire que la classe va depender de l'interface IDao et non pas d'une class*

```java
public class MetierImpl implements IMetier{
    private IDao dao;
    @Override
    public double calcul() {
        return 2 * dao.getValue();
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
```

#### l'injection des dépendances :

*   Par instanciation statique
    ```java
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl();
        metier.setDao(dao);
        System.out.println( "res : " + metier.calcul() );
    }
    ```

* Par instanciation dynamique  
    on crée un fichier `config.txt` danslequel on met les noms des classes.

    ![config.txt](screen%20shots/TP1/Screenshot%202022-04-02%20164223.png)

    puis on peut utiliser ces classes avec ``Class.forName( "NomClasse" )``  
    pour instancier des objets on utilise ``ClassName.newInstance()``  
    on appel les methodes d'un objet avec : ``method.invoke( )``

    ```java
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner( new File( "config.txt" ));
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
    ```

    * En utilisant le Framework Spring

