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
        @Component
        public class MetierImpl implements IMetier{ ... }
        ```        

        ```java
        public static void main(String[] args) {
            ApplicationContext context = new AnnotationConfigApplicationContext("dao", "metier");
            IMetier metier = (IMetier) context.getBean(IMetier.class);
            System.out.println(metier.calcul());
        }
        ```


## [2 - JPA, Hibernate et Spring Data](./TP2%20-%20JPA%2C%20Hibernate%20et%20Spring%20Data/)

On crée un projet spring avec les dependances suivantes :  

![dependances](screen%20shots/TP2/1dependencies.png)

On modifie le fichier `application.properties`, danslequel on spécifie note base de donnée et le port de l'application.  

![application.properties](screen%20shots/TP2/2applicationPropreties.png)

#### Patients

On crée la class persistante Patient avec l'annotaion `@Entity`  
les annotaions `@Data`, `@NoArgsConstructor` et `@AllArgsConstructor` sont de Lombok pour générer automatiquement les getters et setters, ansi que les constructeur avec et sans paramètres.
```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String nom;
    ...
```

On crée une interface `PatientRepository` qui va hériter de `JpaRepository`. Cette interface implemente des methodes qui nous permettent de communiquer avec la base de données, et qui peuvent être personalisées.  
```java
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByMalade(boolean m);
    Page<Patient> findByMalade(boolean m, Pageable pageable);
    List<Patient> findByMaladeAndScoreLessThan(boolean m, int score);
}
```
Pour que notre application soit une application Spring, on ajoute l'annotaion `@SpringBootApplication`.  
Pour faire les tests sur notre base de donées depuis l'application on peut implemeter l'interface `CommandLineRunner` et redefinir la methode `run`.
```java
@SpringBootApplication
public class JpaApplication implements CommandLineRunner {
    @Autowired
    private PatientRepository patientRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception 
    { ... }
}
```

Après le demarrage de l'application, on se dirige vers http://localhost:8082/h2-console/ où on peut se connecter à la base de donnée.   

![h2-console](screen%20shots/TP2/3H2Console.png) 

On verifie que les tests marche bien.  

![patients](screen%20shots/TP2/Screenshot%202022-04-10%20155926.png)  

#### MySQL

Maintenant, pour basculer vers une base de donnée MySQL.  
Il s'uffit d'ajouter la dependance suivante :  
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

Et de modifier le fichier `application.properties`  

![application.properties](screen%20shots/TP2/5applicationPropreties.png)


#### Relations

On ajoute les entités `Medecin`, `RendezVous` et `Consultation`.  

Pour établir les relations entre ces table, on utilise les annotations des `@OneToMany`, `ManyToOne` et `@OneToOne` de __SpringJpa__.  

Par exemple, un patient peut avoir plusieurs rendez-vous. Donc, dans la classe Patient on va ajouter une `Collection` de RendezVous avec l'annotaion `@OneToMany`:  
```java
@OneToMany( mappedBy = "patient", fetch = FetchType.LAZY)
private Collection<RendezVous> rendezVous;
```
>'mappedBy' ici fait référence à l'attribut patient dans la classe RendezVous, qui se traduit par une clé entrangère dans la base de donnée.  

Et dans la classe RendezVous, on va ajouter un attribut Patient avec l'annoation `@ManyToOne`:  

```java
@ManyToOne
private Patient patient;
@ManyToOne
private Medecin medecin;
@OneToOne(mappedBy = "rendezVous")
private Consultation consultation;
```

On fait pareil pour [les autres classes](./TP2%20-%20JPA%2C%20Hibernate%20et%20Spring%20Data/src/main/java/ma/enset/jpaap/entities/).  

On execute l'application, on voit que les table sont bien liées dans la base de donée.  
![hospital_db](./screen%20shots/TP2/hospital_db.png)