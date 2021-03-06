package com.pluralsight.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pluralsight.model.Ride;
import com.pluralsight.service.RideService;
import com.pluralsight.util.ServiceError;

@Controller
public class RideController {

	@Autowired
	private RideService rideService;

	/**
	 * http://localhost:8080/ride_tracker/batch
	 * 
	 * @return La mise � jours de toutes les ride_date en base de donn�es
	 */
	@RequestMapping(value = "/batch", method = RequestMethod.GET)
	public @ResponseBody Object batch() {
		rideService.batch();
		return null;
	}

	/**
	 * http://localhost:8080/ride_tracker/ride
	 * 
	 * @param ride
	 * @return Permet de creer une course
	 */
	@RequestMapping(value = "/ride", method = RequestMethod.POST)
	public @ResponseBody Ride createRide(@RequestBody Ride ride) {
		return rideService.createRide(ride);
	}

	/**
	 * http://localhost:8080/ride_tracker/delete/{id}
	 * 
	 * @param id
	 * @return La suppresssion en base de donn�es de l'id pass� en parametre
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Object delete(@PathVariable(value = "id") Integer id) {
		rideService.deleteRide(id);
		return null;
	}

	/**
	 * http://localhost:8080/ride_tracker/ride/{id}
	 * 
	 * @param id
	 * @return La course portant l'id pass� en param�tre
	 */
	@RequestMapping(value = "/ride/{id}", method = RequestMethod.GET)
	public @ResponseBody Ride getRide(@PathVariable(value = "id") Integer id) {
		return rideService.getRide(id);
	}

	/**
	 * http://localhost:8080/ride_tracker/rides
	 * 
	 * @return la liste des courses lu en base de donn�es
	 */
	@RequestMapping(value = "/rides", method = RequestMethod.GET)
	public @ResponseBody List<Ride> getRides() {
		return rideService.getRides();
	}

	/**
	 * http://localhost:8080/ride_tracker/test
	 * 
	 * @return DataAccessException
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody Object test() {
		throw new DataAccessException("Testing exception thrown") {
		};
	}
	/**
	 * 
	 * @param ex
	 * @return la response de l'exception
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ServiceError> handle(RuntimeException ex){
		ServiceError error = new ServiceError(HttpStatus.OK.value(), ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}

	/**
	 * http://localhost:8080/ride_tracker/ride/{id}
	 * 
	 * @param id
	 * @return La mise � jours de la course partant l'id pass� en parametre
	 */
	@RequestMapping(value = "/ride", method = RequestMethod.PUT)
	public @ResponseBody Ride updateRide(@RequestBody Ride ride) {
		return rideService.updateRide(ride);
	}
}
