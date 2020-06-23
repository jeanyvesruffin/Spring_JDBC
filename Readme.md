# Spring JDBC

Spring JDBC a �t� cr�e pour resoudre les probl�mes de complexit�, de design, de portabilit� et ainsi nous laisser faire le focus sur la partie business.

<!-- TOC -->

- [Spring JDBC](#spring-jdbc)
    - [Installation](#installation)
    - [Configuration dependance et acces � la base de donn�e jdbc](#configuration-dependance-et-acces-�-la-base-de-donn�e-jdbc)
    - [Spring MVC, mise en oeuvres des packages model/ controller/ service/ repository](#spring-mvc-mise-en-oeuvres-des-packages-model-controller-service-repository)
    - [Spring JDBC](#spring-jdbc-1)
    - [Creation d'enregistrement dans la base de donn�e](#creation-denregistrement-dans-la-base-de-donn�e)
    - [Avant de commencer](#avant-de-commencer)
        - [Creation des tables de la base donn�e](#creation-des-tables-de-la-base-donn�e)
        - [Creation des tests et des controllers](#creation-des-tests-et-des-controllers)
        - [Creation d'un appel au service](#creation-dun-appel-au-service)
        - [Creation d'un appel au repository](#creation-dun-appel-au-repository)
    - [Creation d'enregistrement dans la base de donn�e **JdbcTemplate Insert**](#creation-denregistrement-dans-la-base-de-donn�e-jdbctemplate-insert)
    - [Creation d'enregistrement dans la base de donn�e **SimpleJdbcInsert**](#creation-denregistrement-dans-la-base-de-donn�e-simplejdbcinsert)
    - [Lecture des enregistrements de la base de donn�es](#lecture-des-enregistrements-de-la-base-de-donn�es)
        - [Read All](#read-all)
        - [Modify Test](#modify-test)
        - [Externalisation RowMapper](#externalisation-rowmapper)
        - [Create Ride Read](#create-ride-read)
        - [Lecture d'un SimpleJdbcInsert](#lecture-dun-simplejdbcinsert)
    - [Mise � jour (Update) des enregistrements en base de donn�es](#mise-�-jour-update-des-enregistrements-en-base-de-donn�es)
        - [Select One](#select-one)
        - [UpdateOne](#updateone)
        - [Mise � jour de batch (update multiple en une seul requete)](#mise-�-jour-de-batch-update-multiple-en-une-seul-requete)
    - [Suppression d'un enregistrement de la base de donn�es](#suppression-dun-enregistrement-de-la-base-de-donn�es)
        - [Delete JdbcTemplate](#delete-jdbctemplate)
        - [Delete NamedParameterJdbcTemplate](#delete-namedparameterjdbctemplate)
    - [Exception](#exception)
        - [Modification de test pour attraper l'erreur](#modification-de-test-pour-attraper-lerreur)
        - [ExceptionHandler et ServiceError](#exceptionhandler-et-serviceerror)
    - [Transactions](#transactions)
        - [Transaction Manager](#transaction-manager)
    - [BUG FIXE](#bug-fixe)

<!-- /TOC -->


## Installation

Une base de donn�es Mysql est cr�� � l'aide de Mysql Workbench.


## Configuration dependance et acces � la base de donn�e jdbc

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
		
Cr�er un fichier de configuration exemple jdbc-config.xml

- Ajouter le bean dataSource, ou se trouvera la configuration du serveur et jdbcTemplate

	<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
		<property name="url" value="jdbc:mysql://localhost:3306/ride_tracker?useSSL=false" />
		<property name="username" value="root" />
		<property name="password" value="Grimgo37!" />
	</bean>
	<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="dataSource" />
	</bean>
	
Rappelons que la ref="dataSource" mis dans le bean jdbcTemplate correspond � l'id du bean id="dataSource" du dessus

## Spring MVC, mise en oeuvres des packages model/ controller/ service/ repository

1 - Creation des **Pojo** dans le package **model** (ici description d'une course "Ride.java")

2 - Creation package **controller** contenant RideController qui **injecte/ cable les services** ici RideService et mappe les futures demandes (request http).
Ici la requete Http GET appel� sur l'url localhost:3306/ride_tracker/rides nous retournera une list de course.

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
Puis nous definition le contrat de l'interface ici List <Ride> getRides() qui instancie une nouvelle course et la met dans une liste. Le package repository peut etre appele aussi DAO pour Data Acces Objet.

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

1 - On cable (@Autowired) une instance JdbcTemplate dans le fichier de RideRepositoryImpl, qui fait reference � notre beans JdbcTemplate.

	...
	public class RideRepositoryImpl implements RideRepository {
		@Autowired
		private JDBCTemplate jdbcTemplet;	
	}
	...
	
## Creation d'enregistrement dans la base de donn�e
Plusieurs m�thodes s'offre � nous soit � l'aide de:
1. JdbcTemplate Insert
2. SimpleJdbcInsert

## Avant de commencer
### Creation des tables de la base donn�e
Dans l'editeur WorkBench executer le script suivant:

	CREATE TABLE 'ride_tracker'.'ride'(
		'id' INT NOT NULL AUTO_INCREMENT,
		'name' VARCHAR(100) NOT NULL,
		'duration' INT NOT NULL,
		PRIMARY KEY ('id'));
	)

### Creation des tests et des controllers
Creer une methode de test simulant le remplissage de la table ride_tracker qui intancie RestTemplate:pour l'acc�s HTTP c�t� client synchrone. Elle simplifie la communication avec les serveurs HTTP et applique les principes RESTful. Elle g�re les connexions HTTP, laissant le code d'application fournir des URL (avec des variables de mod�le possibles) et extraire les r�sultats.

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


### Creation d'un appel au service
Nous allons creer une nouvelle course lorsque l'on passera par la methode createRide du controlleur utilant la RequesTMethod.PUT

Dans le controller

	// remplacer return null
	@RequestMapping(value = "/ride", method = RequestMethod.PUT)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.create(ride);
	}

Puis � l'aide de l'ide generer la methode creatRide() dans l'interface
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

### Creation d'un appel au repository
Nous alimentons enfin le repository en ajoutant au contrat d'interface la methode createRide(Ride ride)

Ajouter dans le fichier RideRepository

	Ride createRide(Ride ride);

Ajouter dans l'implementation RideRepositoryImpl

	@Override
	public Ride createRide(Ride ride) {
		return null;
	}

## Creation d'enregistrement dans la base de donn�e **JdbcTemplate Insert**

Ajouter � la m�thode public Ride createRide(Ride ride) du fichier RideRepositoryImpl, l'insertion en base de donn�es:

	...
	@Override
	public Ride createRide(Ride ride) {
		jdbcTemplate.update("INSERT INTO ride (name, duration) value (?,?)", ride.getName(), ride.getDuration());
		return null;
	}
	...


## Creation d'enregistrement dans la base de donn�e **SimpleJdbcInsert**

Apres avoir comment� le code precedent.

	// jdbcTemplate.update("INSERT INTO ride (name, duration) value (?,?)", ride.getName(), ride.getDuration());
		
Ajouter les lignes suivantes, l'avantage est que nous retournons l'id de la ligne nouvellement inser�e.

	...
	@Override
	public Ride createRide(Ride ride) {
		// Technique 1 insertion JdbcTemplate
		// jdbcTemplate.update("INSERT INTO ride (name, duration) value (?,?)", ride.getName(), ride.getDuration());
		//Technique 2: insertion d'un objet
		SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
		List<String> columns = new ArrayList<>();
		columns.add("name");
		columns.add("duration");
		insert.setTableName("ride");
		insert.setColumnNames(columns);
		Map<String, Object> data = new HashMap<>();
		data.put("name", ride.getName());
		data.put("duration", ride.getDuration());
		insert.setGeneratedKeyName("id");
		Number key = insert.executeAndReturnKey(data);
		System.out.println("id insert "+ key);
		return null;
	}
	...

## Lecture des enregistrements de la base de donn�es
La lecture est mise en oeuvre � l'aide de JdbcTemplate, RowMapper ou SimpleJdbcCall.

### Read All

1 - Ajouter un id � votre model Ride.java avec ces getters et setters.

	...
	private Integer id;
	...
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	...
	
	
2 - On retire les information hard code du fichier RideRepositoryImpl

		/*
		Ride ride = new Ride();
		ride.setName("Corner Canyon");
		ride.setDuration(120);
		List <Ride> rides = new ArrayList<>();
		rides.add(ride);
		*/

3 - On remplace les informations precedement hard code:

Dans RideRepositoryImpl

	@Override
	public List<Ride> getRides() {
		List<Ride>rides = jdbcTemplate.query("select * from ride", new RowMapper<Ride>() {
			@Override
			public Ride mapRow(ResultSet rs, int rowNum) throws SQLException {
				Ride ride = new Ride();
				ride.setId(rs.getInt("id"));
				ride.setName(rs.getString("name"));
				ride.setDuration(rs.getInt("duration"));
				return ride;
			}
		});
		return rides;
	}

### Modify Test
Nous allons modifier le test de testCreateRide afin que celui-ci nous retourne l'objet cr�� en base de donn�es

Remplacer dans RestControllerTest:

	restTemplate.put("http://localhost:3306/ride_tracker/ride",ride);

Par:

	ride = restTemplate.postForObject("http://localhost:8080/ride_tracker/ride", ride, Ride.class);	


Puis remplacer dans le fichier RideController:

	@RequestMapping(value = "/ride", method = RequestMethod.PUT)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.createRide(ride);
	}

par

	@RequestMapping(value = "/ride", method = RequestMethod.POST)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.createRide(ride);
	}

### Externalisation RowMapper
Creation d'un RowMapper unique que l'on externalise pour une simple course (getSingleRide)

1 - Creer un package dans repository que l'on nommera util
2 - Creer une classe implementant RowMapper (attention issu de la librairy spring-core et non swing)
3 - Puis copier/coller la partie rowmapper du fichier RideRepositoryImpl dans la classe precedemment cree:


	public class RideRowMapper implements RowMapper<Ride> {
		@Override
		public Ride mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ride ride = new Ride();
			ride.setId(rs.getInt("id"));
			ride.setName(rs.getString("name"));
			ride.setDuration(rs.getInt("duration"));
			return ride;
		}
	}
	
4 - Supprimer le rowMapper du fichier RideRepositoryImpl et ajouter le new RideRowMapper:

	...
	@Override
	public List<Ride> getRides() {
		List<Ride>rides = jdbcTemplate.query("select * from ride", new RideRowMapper());
		return rides;
	}


### Create Ride Read
Dans le fichier RideRepositoryImpl remplacer :

	jdbcTemplate.update("INSERT INTO ride (name, duration) value (?,?)", ride.getName(), ride.getDuration());

Par:

	@Override
	public Ride createRide(Ride ride) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement("INSERT INTO ride (name, duration) value (?,?)", new String [] {"id"});
				ps.setString(1, ride.getName());
				ps.setInt(2, ride.getDuration());
				return ps;
			}
		}, keyHolder);
		Number id = keyHolder.getKey();
		return getRide(id.intValue());
	}
	
Puis creer la methode getRide():


	public Ride getRide(Integer id) {
		Ride ride = jdbcTemplate.queryForObject("select * from ride where id = ?", new RideRowMapper(), id);
		return ride;
	}

### Lecture d'un SimpleJdbcInsert

1 - Basculer dans le fichier RideRepositoryImpl la technique precedente et revenir � notre SimpleJdbsInsert, telquel.

	SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
	insert.setGeneratedKeyName("id");
	Map<String, Object> data = new HashMap<>();
	data.put("name", ride.getName());
	data.put("duration", ride.getDuration());
	List<String> columns = new ArrayList<>();
	columns.add("name");
	columns.add("duration");
	insert.setTableName("ride");
	insert.setColumnNames(columns);
	Number key = insert.executeAndReturnKey(data);
	return getRide(key.intValue());

Nous avons desormais chang� notre initialisation de repository par un PrepareStatementCreator qui nous retourne la course a travers ca cl�.

## Mise � jour (Update) des enregistrements en base de donn�es

Nous utiliserons JdbcTemplate ainsi que batchUpdate qui nous permettra d'envoyer en une requete plusieurs update en base de donn�es.

### Select One

1 - On Override la methode getRide du fichier RideRepositoryImpl et l'ajoutons au contrat d'interface.

Dans RideRepositoryImpl

	...
	@Override
		public Ride getRide(Integer id) {
			Ride ride = jdbcTemplate.queryForObject("SELECT * FROM ride WHERE id = ?", new RideRowMapper(), id);
			return ride;
		}
	...
		
Dans RideRepository

	...
	Ride getRide(Integer id);
	...

2 - Idem pour le package service

Dans RideServiceImpl
	
	...
	@Override
	public Ride getRide(Integer id) {
		return rideRepository.getRide(id);
	}
	...
	
Dans RideService
	
	...
	Ride getRide(Integer id);
	...
	
3 - Compl�ter le controller

Dans le RideController, ajouter la methode qui permettra d'envoyer en entete de requete Http la requete de demande d'une course avec l'id en paramatre.

	...
	@RequestMapping(value = "/ride/{id}", method = RequestMethod.GET)
	public @ResponseBody Ride getRide(@PathVariable(value="id") Integer id) {
		return rideService.getRide(id);
	}
	...
	
4 - Ajoutons le test associ�:

	...
	@Test(timeout=3000)
	public void testGetRide() {
		RestTemplate restTemplate = new RestTemplate();
		Ride ride = restTemplate.getForObject("http://localhost:8080/ride_tracker/ride/1", Ride.class);
		System.out.println("Ride name :" + ride.getName());
	}
	...
	
### UpdateOne

1 - Ajouter le test
	
	...
	@Test(timeout=3000)
	public void testUpdateRide() {
		RestTemplate restTemplate = new RestTemplate();
		Ride ride = restTemplate.getForObject("http://localhost:8080/ride_tracker/ride/1", Ride.class);
		ride.setDuration(ride.getDuration() + 1);
		restTemplate.put("http://localhost:8080/ride_tracker/ride", ride);
		System.out.println("Ride name :" + ride.getName());
	}
	...

2 - Ajouter au RideController la requete de mise � jour

	...
	@RequestMapping(value = "/ride}", method = RequestMethod.PUT)
	public @ResponseBody Ride updateRide(@RequestBody Ride ride) {
		return rideService.updateRide(ride);
	}
	...
	
3 - Ajouter au contrat d'interface RideService

	...
	Ride updateRide(Ride ride);
	...
	
4 - On ajoute l'implementation de l'interface dans le fichier RideServiceImpl

	...
	@Override
	public Ride updateRide(Ride ride) {
		return rideRepository.updateRide(ride);
	}
	...
	
5 - Ajouter au contrat d'interface RideRepository
	
	...
	Ride updateRide(Ride ride);
	...
	
6 - On ajoute l'implementation de l'interface dans le fichier RideRepositoryImpl

	...
	@Override
	public Ride updateRide(Ride ride) {
		jdbcTemplate.update("UPDATE ride SET name = ?, duration = ? WHERE id = ?", ride.getName(), ride.getDuration(), ride.getId());
		return ride;
	}
	...


### Mise � jour de batch (update multiple en une seul requete)

1 - On ajoute une colonne � notre base de donn�e

	USE ride_tracker;
	ALTER TABLE ride ADD ride_date DATETIME AFTER duration;

2 - Le but est de remplir en une seul requete toutes les lignes en y ajoutant une ride_date.

Dans notre fichier RestControllerTest, ajouter le test suivant:

	...
	@Test(timeout=3000)
	public void testBatchUpdate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject("http://localhost:8080/ride_tracker/batch", Object.class);
	}
	...

3 - Ajouter le code suivant dans le RideController ou nous appelons la future methode de service batch que nous crerons apres:
	
	...
	@RequestMapping(value = "/batch", method = RequestMethod.GET)
	public @ResponseBody Object batch() {
		rideService.batch();
		return null;
	}
	...

4 - Ajouter au contrat d'interface RideService

	...
	void batch();
	...
	
5 -  On ajoute l'implementation de l'interface dans le fichier RideServiceImpl avec la future methode de repository updateRides(pairs) que nous crerons apres:

	...
	@Override
	public void batch() {
		List<Ride> rides = rideRepository.getRides();
		List<Object[]> pairs = new ArrayList<>();
		for (Ride ride : rides) {
			Object[]tmp = {new Date(), ride.getId()};
			pairs.add(tmp);
		}
		rideRepository.updateRidespairs(pairs);
	}
	...
	
6 - Ajouter au contrat d'interface RideRepository

	...
	void updateRides(List<Object[]> pairs);
	...
	
7 - On ajoute l'implementation de l'interface dans le fichier RideRepositoryImpl

	...
	@Override
	public void updateRides(List<Object[]> pairs) {
		jdbcTemplate.batchUpdate("UPDATE ride SET ride_date = ? WHERE id = ?", pairs);	
	}
	...

## Suppression d'un enregistrement de la base de donn�es
Nous utiliserons JdbcTemplate ou NamedParameterJdbcTemplate pour supprimer des donn�e dans la base de donn�es.

### Delete JdbcTemplate

1 - Dans le fichier RestControllerTest, ajouter un test sur la suppression de donn�e en base:

	...
	@Test(timeout=3000)
	public void testDelete() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://localhost:8080/ride_tracker/delete/18");
	}
	...

2 - Dans RideController

	...
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Object delete(@PathVariable(value="id") Integer id) {
		rideService.deleteRide(id);
		return null;
	}
	...
	
3 - Ajouter au contrat d'interface RideService

	...
	void deleteRide(Integer id);
	...

4 - On ajoute l'implementation de l'interface dans le fichier RideServiceImpl

	...
	@Override
	public void deleteRide(Integer id) {
		rideRepository.deleteRide(id);
	}
	...
	
5 - Ajouter au contrat d'interface RideRepository

	...
	void deleteRide(Integer id);
	...
	
6 - On ajoute l'implementation de l'interface dans le fichier RideRepositoryImpl

	...
	@Override
	public void deleteRide(Integer id) {
		jdbcTemplate.update("DELETE FROM ride WHERE id = ?", id);
	}
	...
	
### Delete NamedParameterJdbcTemplate
Commenter au prealable la precedente technique de suppression dans le fichier RideRepositoryImpl public void deleteRide(Integer id)

1 - Ajouter dans le fichier RideRepositoryImpl:

	...
	@Override
	public void deleteRide(Integer id) {
		NamedParameterJdbcTemplate namesTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		// Creation d'une map avec comme name value pairs (Object)
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		namesTemplate.update("DELETE FROM ride WHERE id = :id", paramMap);
	}
	...

Remarquons ici que le paramtre id est transmis � l'aide d'une Map

2 - Modifions notre test en remplaceant l'id � supprimer par un id existant, maintenant 17 . Dans le fichier RestControllerTest

	...
	@Test(timeout=3000)
	public void testDelete() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://localhost:8080/ride_tracker/delete/17");
	}
	...
	
## Exception
Nous verrons ici comment traiter les exceptions.

### Modification de test pour attraper l'erreur

1 - Ajouter le test suivant dans le fichier RestControllerTest

	...
	@Test(timeout=3000)
	public void testException() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject("http://localhost:8080/ride_tracker/test", Ride.class);
	}
	...
	
2 - Ajouter dans le fichier RideController

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody Object test() {
		throw new DataAccessException("Testing exception thrown") {
			};
		}		
	
Si l'on test cela nous rencontrons une erreur. Mais son traitement n'est pas optimal.

### ExceptionHandler et ServiceError

1 - Ajouter un package exemple com.pluralsight.util

2 - Ajouter une classe ServiceException dans ce package avec getters, setters, constructeur sans parametre et constructeur avec parametres

	public class ServiceError {
		private int code;
		private String message;
		public ServiceError() {
			super();
		}
		public ServiceError(int code, String message) {
			super();
			this.code = code;
			this.message = message;
		}
		public int getCode() {
			return code;
		}
		public String getMessage() {
			return message;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}

3 - Ajouter dans le fichier RideController

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ServiceError> handle(RuntimeException ex){
		ServiceError error = new ServiceError(HttpStatus.OK.value(), ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}

Maintenant l'erreur est relev�e mais le test s'execute normalement.

## Transactions
Les transactions permettent de faire de multiple appel � jdbcTemplate � l'aide de DataSourceTransactionManager, cad, que l'on peut enchainer les requetes.

### Transaction Manager

1 - Dans le fichier RideServiceImpl modifier la methode public void batch() telque:

	@Override
	public void batch() {
		List<Ride> rides = rideRepository.getRides();
		List<Object[]> pairs = new ArrayList<>();
		for (Ride ride : rides) {
			Object[] tmp = { new Date(), ride.getId() };
			pairs.add(tmp);
		}
		rideRepository.updateRides(pairs);
		throw new DataAccessException("Testing Exception Handling") {
		};
	}

2 - Dans le fichier de configuration jdbc-config.xml

Cliquer sur l'onglet Namespaces et cocher tx - http://www.springframework.org/schema/tx

Puis ajouter les deux beans suivants:

	<tx:annotation-driven transaction-manager="transactionManager"/>

	<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
			<property name="dataSource" ref="dataSource"/>
	</bean>
	
3 - Dans le fichier RideServiceImpl. Ajouter sur la methode public void batch() l'annotation @Transactional

4 - S'il on test d�sormais la m�thode testBatchUpdate.

Nous pouvons observer que l'ors de l'exception l'application ne plante pas et l'exceution de batchUpdate ne fait rien en base de donn�e.

Dans une application plus complexe, si je mettais � jour 3, 4, 5 tables de base de donn�es, tout ce que j'avais � faire serait de mettre cette annotation en haut et cela annulerait toutes nos modifications si une exception �tait lev�e quelque part en cours de route.

##  BUG FIXE

SEVERE: Servlet.service() du Servlet [rideTrackerServlet] dans le contexte au chemin [/ride_tracker] a retourn� une exception [Request processing failed; nested exception is org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection; nested exception is java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed] avec la cause
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed

Solution:

Desinstallation/ Reinstallation de MySQL � l'adresse :

https://dev.mysql.com/

Toujours en erreur

SEVERE: Servlet.service() du Servlet [rideTrackerServlet] dans le contexte au chemin [/ride_tracker] a retourn� une exception [Request processing failed; nested exception is org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection; nested exception is java.sql.SQLException: The server time zone value 'Paris, Madrid (heure d??t?)' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the 'serverTimezone' configuration property) to use a more specifc time zone value if you want to utilize time zone support.] avec la cause
com.mysql.cj.exceptions.InvalidConnectionAttributeException: The server time zone value 'Paris, Madrid (heure d??t?)' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the 'serverTimezone' configuration property) to use a more specifc time zone value if you want to utilize time zone support.

Ajout dans le fichier de configuration my.ini de la ligne suivante:

	# Set default time zone
	default-time-zone = '+02:00'
	
Bien penser � arreter et redemarrer mysql dans le menu windows tapper services puis chercher le afin de le restart.
Attention le service porte le nom defini lors de l'installation de mysql ici SQLAuthority
	

Le jour suivant: "Toujours en  erreur"
SEVERE: Servlet.service() du Servlet [rideTrackerServlet] dans le contexte au chemin [/ride_tracker] a retourn� une exception [Request processing failed; nested exception is org.springframework.dao.DataAccessResourceFailureException: Error retrieving database meta-data; nested exception is org.springframework.jdbc.support.MetaDataAccessException: Could not get Connection for extracting meta-data; nested exception is org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection; nested exception is java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed] avec la cause
java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed

SOLUTION Il faut tous simplement allumer le server mysql workbench

ERREUR: WARNING: Request method 'PUT' not supported


