# KSSIBA_JEE
*Cette repo est une collection des traveaux pratiques JEE - ENSET 2021/2022*

### Comptes Rendues :  
*   #### [TP1 : Inversion de contrôle et Injection des dépendances](#1---inversion-de-contrôle-et-injection-des-dépendances) [[code source](./TP1%20-%20IOC%20et%20Injection%20des%20d%C3%A9pendances/)]

*   #### [TP2 : JPA, Hibernate et Spring Data](#2---jpa-hibernate-et-spring-data) [[code source](./TP2%20-%20JPA%2C%20Hibernate%20et%20Spring%20Data/)]  

*   #### [TP3 : Spring MVC Thymeleaf](#3---spring-mvc-thymeleaf) [[code source](./TP3%20-%20Spring%20MVC%20Thymeleaf/)]  

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



## [3 - Spring MVC Thymeleaf](./TP3%20-%20Spring%20MVC%20Thymeleaf/)

Dans cette activité pratique on va créer une application Web basée sur Spring MVC, Spring Data JPA et Spring Security qui permet de gérer des patients.

### Dépendences
Pour les dependences on est besoin de __Spring Data Jpa__, __Spring Web__, __Spring Security__, __Thymeleaf__, __MySQL Driver__ et __Lombok__.

### Entités
On crée la classe persistante Patient, chaque patient est défini par:  
- Son id
- Son nom
- Sa date naissance
- Un attribut qui indique si il est en malade ou non  

Pour valider qu'un champs ne doit pas être vide, on ajoute l'annotation `@NotEmpty`  

```java
@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    @NotEmpty
    private String nom;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat( pattern = "yyyy-mm-dd" )
    private Date dateNaissance;
    private boolean malade;
}
```

### Ropositories
En crée une repository patient, et on ajoute une méthode qui nous permettra de faire une recherche paginée des patients.

```java
public interface PatientRepository extends JpaRepository<Patient,Long> {
    Page<Patient> findByNomContains(String kw, Pageable pageable);
}
```
  
### Web
Pour répondre aux requêtes des clients, en ajoute la couche web dans laquel on definit des controlleurs ( classe avec l'annotation `@Contoller` ). 
Dans notre cas on a la classe `PatientController` qui contient des méthodes qui retourne des vue pour chaque route.

```java
@Controller
@AllArgsConstructor
public class PatientController {
    PatientRepository patientRepository;

    @GetMapping("/user/index")
    public String patients(Model model,
                            @RequestParam(name="page",defaultValue = "0") int page,
                            @RequestParam(name = "size",defaultValue = "5") int size,
                            @RequestParam(name = "keyword",defaultValue = "") String keyword){
        Page<Patient> pagePatients = patientRepository.findByNomContains( keyword, PageRequest.of(page,size));
        model.addAttribute( "listPatients", pagePatients.getContent() );
        model.addAttribute("pages", pagePatients.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "patients";
    }
```

Pour communiquer avec les vues, on utilise un `Model` que l'on utilise pour passer des paramètres.


### Vues
On génère les pages html à partir des vues avec le moteur de template `thymeleaf`.  
Donc, pour chaque page on va créer une vue qui est une page html avec le namespace suivant `xmlns:th="http://www.thymeleaf.org"`.  

Pour ne pas répéter la bar de navigation à chaque fois, on peut créer une page `template` qu'on va utiliser dans toutes les vues.  
Pour cela on crée un fichier `template.html` avec le namespace `xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"` à l'addition de `xmlns:th`.  
Et une balise `section` avec l'attribut `layout:fragment`, qui indique un fragment dans lequel on peut injecter du code html à partir des autres vues.  

>template.html
```html
<!DOCTYPE html>
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
...
<section layout:fragment="content1"></section>
...
</html>
```

>Dans les autre vues on ajoute l'attribut ``layout:decorate`` en specifiant la page à utiliser comme une base.  
Puis on definit le contenu du fragment avec ``layout:fragment``.
```html
<html lang="en"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="template">
...
<div layout:fragment="content1">
    ...
</div>
...
</html>
```

### Security
Dans la partie sécurité, on crée les entités `AppUser` et `AppRole` avec leur repositories pour les utilisateurs et les rôles.


Pour configurer l'authentification et definir les droit d'accés, on crée une classe `SecurityConfig` qui hérite de `WebSecurityConfigurerAdapter`, avec les annotations : `@Configuration` et `@EnableWebSecurity`  

Puis on redéfinit les deux méthodes : [voir l'implementation](./src/main/java/ma/enset/patientsmvc/sec/SecurityConfig.java)  
```java
protected void configure(AuthenticationManagerBuilder auth)
protected void configure(HttpSecurity http)
```

Pour l'authentification on utilise `UserDetailsService` pour cela on crée notre propre implementation de cette interface. On doit donc redéfinir la méthode `loadUserByUsername` qui permet de récupérer des données relatives à l'utilisateur.

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private SecurityService securityService;

    public UserDetailsServiceImpl(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = securityService.loadUserByUserName(username);

        Collection<GrantedAuthority> authorities = appUser.getAppRoles()
                .stream().map( role-> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        User user = new User( appUser.getUsername(), appUser.getPassword(), authorities);
        return user;
    }
}
```


Finalement, dans les vues, pour afficher un contenu différent selon le rôle d'utilisateur, on doit premièrement utiliser l'espace de nom : `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`.  
Par exemple, si on veut afficher un contenu, à un utilisateur avec le rôle admin, on peut faire : 
```html
<div sec:authorize="hasAuthority('ADMIN')">.
```
et si on veut autoriser tous les utilisateur authentifiés on utilise : `sec:authorize="isAuthenticated()"`

### Screen Shots  

__la liste des patients en tant qu'administrateur__  
![liste des etudiant as admin](./screen%20shots/TP3/listPatients.png)  


__la liste des patients en tant qu'utilisateur__    
![liste des etudiant as admin](./screen%20shots/TP3/listPatients2.png)
