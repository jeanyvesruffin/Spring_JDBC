package com.pluralsight.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.pluralsight.model.Ride;
import com.pluralsight.repository.util.RideRowMapper;

@Repository("rideRepository")
public class RideRepositoryImpl implements RideRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Ride createRide(Ride ride) {

		// Technique 1 insertion JdbcTemplate
		// jdbcTemplate.update("INSERT INTO ride (name, duration) value (?,?)",
		// ride.getName(), ride.getDuration());

		// Technique 2: insertion d'un objet
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

		// Technique 3: Create Ride Read
//		KeyHolder keyHolder = new GeneratedKeyHolder();
//		jdbcTemplate.update(new PreparedStatementCreator() {
//			@Override
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement ps = con.prepareStatement("INSERT INTO ride (name, duration) value (?,?)", new String [] {"id"});
//				ps.setString(1, ride.getName());
//				ps.setInt(2, ride.getDuration());
//				return ps;
//			}
//		}, keyHolder);
//		
//		Number id = keyHolder.getKey();
//		
//		return getRide(id.intValue());
	}

	// Technique permettant de supprimer une ligne de la base de donnée suivant un
	// parametre donnée
	@Override
	public void deleteRide(Integer id) {
		NamedParameterJdbcTemplate namesTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		// Creation d'une map avec comme name value pairs (Object)
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("id", id);
		namesTemplate.update("DELETE FROM ride WHERE id = :id", paramMap);
	}

	@Override
	public Ride getRide(Integer id) {
		Ride ride = jdbcTemplate.queryForObject("SELECT * FROM ride WHERE id = ?", new RideRowMapper(), id);
		return ride;
	}

	@Override
	public List<Ride> getRides() {
//		Ride ride = new Ride();
//		ride.setName("Corner Canyon");
//		ride.setDuration(120);
//		List <Ride> rides = new ArrayList<>();
//		rides.add(ride);
		List<Ride> rides = jdbcTemplate.query("select * from ride", new RideRowMapper());
		return rides;
	}

	@Override
	public Ride updateRide(Ride ride) {
		jdbcTemplate.update("UPDATE ride SET name = ?, duration = ? WHERE id = ?", ride.getName(), ride.getDuration(),
				ride.getId());
		return ride;
	}

	// Technique permettant de supprimer une ligne de la base de donnée suivant son
	// id
	/*
	 * @Override public void deleteRide(Integer id) {
	 * jdbcTemplate.update("DELETE FROM ride WHERE id = ?", id); }
	 */

	@Override
	public void updateRides(List<Object[]> pairs) {
		jdbcTemplate.batchUpdate("UPDATE ride SET ride_date = ? WHERE id = ?", pairs);
	}

}
