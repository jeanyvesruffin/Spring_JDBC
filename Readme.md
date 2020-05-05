# Spring JDBC

Spring JDBC a été crée pour resoudre les problèmes de complexité, de design, de portabilité et ainsi nous laisser faire le focus sur la partie business.


## Installation

Une base de données Mysql est créé à l'aide de Mysql Workbench.


## Configuration dependance et acces à la base de donnée jdbc

Ajout des dependances Maven au fichier .pom

	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<version>8.0.20</version>
	</dependency>
		<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-jdbc</artifactId>
		<version>5.2.6.RELEASE</version>
		</dependency>
		
Créer un fichier de configuration exemple jdbc-config.xml

- Ajouter le bean dataSource, ou se trouvera la configuration du serveur et jdbcTemplate

	<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
		<property name="url" value="jdbc:mysql://localhost:3306/ride_tracker?useSSL=false" />
		<property name="username" value="root" />
		<property name="password" value="password" />
	</bean>
	<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="dataSource" />
	</bean>
	
Rappelons que la ref="dataSource" mis dans le bean jdbcTemplate correspond à l'id du bean id="dataSource" du dessus

## Spring MVC, mise en oeuvres des packages model/ controller/ service/ repository

1 - Creation des **Pojo** dans le package **model** (ici description d'une course "Ride.java")
2 - Creation package **controller** contenant RideController qui **injecte/ cable les services** ici RideService et mappe les futures demandes (request http).
Ici la requete Http GET appelé sur l'url localhost:3306/ride_tracker/rides nous retournera une list de course.

	...
	@Controller
	public class RideController{
		@Autowired
		private RideService rideService;
		
		@RequestMapping(value = "/rides", method = RequestMethod.GET)
		public @ResponseBody List<Ride>getRides();
			return rideService.getRides();
		}
	}
	...

3 - Creation package **service** contenant notre **logic business** ici RideServiceImpl. Implementant l'interface RideService, contrat d'interface qui contient la methode List< Ride > getRides().
Nous cablons (@Autowired) le Repository.
Puis nous definition le contrat de l'interface ici List <Ride> getRides() 

Interface RideService

	...
	public interface RideService {
		List<Ride> getRides();
	}
	...

Implementation de l'interface RideServiceImpl

	...
	@Service("rideService")
	public class RideServiceImpl implements RideService {
		@Autowired
		private RideRepository rideRepository;
		@Override
		public List<Ride>getRide(){
			return rideRepository.getRides();
		}
	}
	

4 - Creation package **repository** contenant nos data ici RideRepositoryImpl . Implementant l'interface RideRepository, contrat d'interface qui contient la methode List< Ride > getRides().
Puis nous definition le contrat de l'interface ici List <Ride> getRides() qui instancie une nouvelle course et la met dans une liste.

Interface RideRepository

	...
	public interface RideRepository {
		List<Ride> getRides();
	}
	...
	
Implementation de l'interface RideRepositoryImpl

	...
	@Repository("rideRepository")
	public class RideRepositoryImpl implements RideRepository {
		@Override
		public List<Ride> getRides() {
			Ride ride = new Ride();
			ride.setName("Corner Canyon");
			ride.setDuration(120);
			List <Ride> rides = new ArrayList<>();
			rides.add(ride);
			return rides;
		}
	}
	...
	
## Spring JDBC

1 - On cable (@Autowired) une instance JdbcTemplate dans le fichier de RideRepositoryImpl, qui fait reference à notre beans JdbcTemplate.

	...
	public class RideRepositoryImpl implements RideRepository {
		@Autowired
		private JDBCTemplate jdbcTemplet;	
	}
	...
	
## Creation d'enregistrement dans la base de donnée
Plusieurs méthodes s'offre à nous soit à l'aide de:
1. JdbcTemplate Insert
2. SimpleJdbcInsert
3. Object Relational Mapping 

## Avant de commencer
##### Creation des tables de la base donnée
Dans l'editeur WorkBench executer le script suivant:

	CREATE TABLE 'ride_tracker'.'ride'(
		'id' INT NOT NULL AUTO_INCREMENT,
		'name' VARCHAR(100) NOT NULL,
		'duration' INT NOT NULL,
		PRIMARY KEY ('id'));
	)

##### Creation des tests et des controllers
Creer une methode de test simulant le remplissage de la table ride_tracker qui intancie RestTemplate:pour l'accès HTTP côté client synchrone. Elle simplifie la communication avec les serveurs HTTP et applique les principes RESTful. Elle gère les connexions HTTP, laissant le code d'application fournir des URL (avec des variables de modèle possibles) et extraire les résultats.

Pour le test ajouter:

	@Test(timeout=3000)
	public void testCreateRide() {
		//Instance RestTemplate
		RestTemplate restTemplate = new RestTemplate();
		//Creation d'une course
		Ride ride = new Ride();
		ride.setName("Course de Tours");
		ride.setDuration(33);
		restTemplate.put("http://localhost:3306/ride_tracker/ride",ride);
	}
		
Sur le controlleur ajouter:

	@RequestMapping(value = "/ride", method = RequestMethod.PUT)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return null;
	}


##### Creation d'un appel au service
Nous allons creer une nouvelle course lorsque l'on passera par la methode createRide du controlleur utilant la RequesTMethod.PUT

Dans le controller

	// remplacer return null
	@RequestMapping(value = "/ride", method = RequestMethod.PUT)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.create(ride);
	}

Puis à l'aide de l'ide generer la methode creatRide() dans l'interface
Puis generer dans l'implementation de l'interface la methode create(ride) (@Override)

Ajouter dans l'interface RideService

	...
	Ride createRide(Ride ride);
	...

Ajouter dans l'implementation RideServiceImpl

	...
	@Override
		public Ride createRide(Ride ride) {		
			return rideRepository.createRide(ride);
		}
	...

##### Creation d'un appel au repository
Nous alimentons enfin le repository en ajoutant au contrat d'interface la methode createRide(Ride ride)

Ajouter dans le fichier RideRepository

	Ride createRide(Ride ride);

Ajouter dans l'implementation RideRepositoryImpl

	@Override
	public Ride createRide(Ride ride) {
		return null;
	}

## Creation d'enregistrement dans la base de donnée **JdbcTemplate Insert**

##  BUG FIXE

SEVERE: Servlet.service() du Servlet [rideTrackerServlet] dans le contexte au chemin [/ride_tracker] a retourné une exception [Request processing failed; nested exception is org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection; nested exception is java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed] avec la cause
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed

