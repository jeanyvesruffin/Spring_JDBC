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
	
2 - 