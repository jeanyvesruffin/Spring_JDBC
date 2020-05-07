package com.pluralsight.service;

import java.util.List;

import com.pluralsight.model.Ride;

public interface RideService {

	void batch();

	Ride createRide(Ride ride);

	void deleteRide(Integer id);

	Ride getRide(Integer id);

	List<Ride> getRides();

	Ride updateRide(Ride ride);

}