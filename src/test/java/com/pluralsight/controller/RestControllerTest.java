package com.pluralsight.controller;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.pluralsight.model.Ride;

import org.junit.Test;

public class RestControllerTest {

	/**
	 * Test permettant de retourner les information de la course id = 1
	 */
	@Test(timeout = 3000)
	public void testGetRide() {
		RestTemplate restTemplate = new RestTemplate();
		Ride ride = restTemplate.getForObject("http://localhost:8080/ride_tracker/ride/1", Ride.class);
		System.out.println("Ride name :" + ride.getName());
	}

	/**
	 * Test permettant de mettre à jour la durée de la course numero 1 en ajoutant 1
	 * à la durée
	 */
	@Test(timeout = 3000)
	public void testUpdateRide() {
		RestTemplate restTemplate = new RestTemplate();
		Ride ride = restTemplate.getForObject("http://localhost:8080/ride_tracker/ride/1", Ride.class);
		ride.setDuration(ride.getDuration() + 1);
		restTemplate.put("http://localhost:8080/ride_tracker/ride", ride);
		System.out.println("Ride name :" + ride.getName());
	}

	/**
	 * Test permettant de retourner la liste de course
	 */
	@Test(timeout = 3000)
	public void testGetRides() {
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<List<Ride>> ridesResponse = restTemplate.exchange("http://localhost:8080/ride_tracker/rides",
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Ride>>() {
				});
		List<Ride> rides = ridesResponse.getBody();

		for (Ride ride : rides) {
			System.out.println("Ride name: " + ride.getName());
		}
	}

	/**
	 * Test permettant de mettre à jours toutes les ride_date
	 */
	@Test(timeout = 3000)
	public void testBatchUpdate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject("http://localhost:8080/ride_tracker/batch", Object.class);
	}

	/**
	 * Test de la creation d'une course en base de données
	 */
	@Test(timeout = 3000)
	public void testCreateRide() {
		// Instance RestTemplate
		RestTemplate restTemplate = new RestTemplate();
		// Creation d'une course
		Ride ride = new Ride();
		ride.setName("Course de fin2");
		ride.setDuration(40);
		// put sur l'url qui correspond au controleur PUT A l'inverse lors du test
		// testGetRides
		// c'est l'url qui pointe sur le controleur GET
		ride = restTemplate.postForObject("http://localhost:8080/ride_tracker/ride", ride, Ride.class);
		System.out.println("Ride: " + ride);
	}

	/**
	 * Test permettant de supprimer la ligne numero 18 de la base de données
	 */
	@Test(timeout = 3000)
	public void testDelete() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://localhost:8080/ride_tracker/delete/17");
	}

	/**
	 * Test pour essai exception
	 */
	@Test(timeout = 3000)
	public void testException() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject("http://localhost:8080/ride_tracker/test", Ride.class);
	}
}
