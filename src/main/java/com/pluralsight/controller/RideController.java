package com.pluralsight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pluralsight.model.Ride;
import com.pluralsight.service.RideService;

@Controller
public class RideController {

	@Autowired
	private RideService rideService;
	
	/**
	 *  http://localhost:8080/ride_tracker/ride
	 * @param ride
	 * @return Permet de creer une course
	 */
	@RequestMapping(value = "/ride", method = RequestMethod.POST)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.createRide(ride);
	}
	
	/**
	 * http://localhost:8080/ride_tracker/rides
	 * @return la liste des courses lu en base de données
	 */
	@RequestMapping(value = "/rides", method = RequestMethod.GET)
	public @ResponseBody List<Ride> getRides() {
		return rideService.getRides();
	}
	
	/**
	 * http://localhost:8080/ride_tracker/ride/{id}
	 * @param id
	 * @return La course portant l'id passé en paramètre
	 */
	@RequestMapping(value = "/ride/{id}", method = RequestMethod.GET)
	public @ResponseBody Ride getRide(@PathVariable(value="id") Integer id) {
		return rideService.getRide(id);
	}
	
	/**
	 * http://localhost:8080/ride_tracker/ride/{id}
	 * @param id
	 * @return La mise à jours de la course partant l'id passé en parametre 
	 */
	@RequestMapping(value = "/ride}", method = RequestMethod.PUT)
	public @ResponseBody Ride updateRide(@RequestBody Ride ride) {
		return rideService.updateRide(ride);
	}
		
}
