package com.pluralsight.repository;

import java.util.List;

import com.pluralsight.model.Ride;

public interface RideRepository {

	Ride createRide(Ride ride);

	void deleteRide(Integer id);

	Ride getRide(Integer id);

	List<Ride> getRides();

	Ride updateRide(Ride ride);

	void updateRides(List<Object[]> pairs);

}