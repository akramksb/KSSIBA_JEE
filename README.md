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
*c'est à dire que la classe va depender de l'interface IDao et non pas d'une classe*

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
    On crée un fichier `config.txt` danslequel on met les noms des classes.

    ![config.txt](screen%20shots/TP1/configtxt.png)

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
    1.  version XML

        On crée un fichier XML `Spring Config` que l'on appel `applicationContext.xml` danslequel on ajoute deux beans
        ```xml
            <bean id="dao" class="dao.DaoImpl"></bean>
            <bean id="metier" class="metier.MetierImpl">
                <property name="dao" ref="dao"></property>
            </bean>
        ```

        ```java
        public static void main(String[] args) {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            IMetier metier = (IMetier) context.getBean( "metier" );
            System.out.println(metier.calcul());
        }
        ```

    1.  version annotations  
        On ajoute l'annotaion `@Component` aux classes *DaoImpl* et *MetierImpl*. Puis on utilise l'annotaion `@Autowired` pour l'attribut dao de la classe *MetierImpl*, ou bien on crée un constructeur avec parametres.

        ```java
        public static void main(String[] args) {
            ApplicationContext context = new AnnotationConfigApplicationContext("dao", "metier");
            IMetier metier = (IMetier) context.getBean(IMetier.class);
            System.out.println(metier.calcul());
        }
        ```


## [2 - JPA, Hibernate et Spring Data](./TP2%20-%20JPA%2C%20Hibernate%20et%20Spring%20Data/)