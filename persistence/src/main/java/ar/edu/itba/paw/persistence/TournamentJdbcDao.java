package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TournamentDao;
import ar.edu.itba.paw.model.Pair;
import ar.edu.itba.paw.model.Tournament;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.Elo;
import ar.edu.itba.paw.model.enums.Region;
import ar.edu.itba.paw.model.enums.Structure;
import ar.edu.itba.paw.model.filters.TournamentFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Repository
public class TournamentJdbcDao implements TournamentDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final SimpleJdbcInsert jfbcInsertParticipant;
    private final SimpleJdbcInsert jfbcInsertMatch;
    //private final SimpleJdbcInsert jdbcInsertTeam;

    @Autowired
    public TournamentJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tournament")
                .usingGeneratedKeyColumns("id");
        this.jfbcInsertParticipant = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participant_user");
        //this.jdbcInsertTeam = new SimpleJdbcInsert(jdbcTemplate)
        //        .withTableName("participant_team");
        this.jfbcInsertMatch = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("match");
    }

    private static final RowMapper<Tournament> ROW_MAPPER = (rs, rowNum) -> new Tournament(rs.getLong("id"),
            rs.getLong("creatorid"), rs.getString("name"), rs.getLong("gameid"), Region.valueOf(rs.getString("region")),
            Elo.valueOf(rs.getString("elo")), rs.getDate("startdate").toLocalDate(), rs.getDate("enddate").toLocalDate(),
            rs.getString("format"), Structure.valueOf(rs.getString("structure")), rs.getInt("max_participants"));

    private static final RowMapper<User> ROW_MAPPER_USER = (rs, rowNum) -> new User(rs.getLong("userId"), rs.getString("username"), rs.getString("email"));


    @Override
    public Optional<Tournament> findById(Long id) {
        return jdbcTemplate.query("SELECT * FROM tournament WHERE id = ?", ROW_MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Tournament> findTournaments(TournamentFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tournament t WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter.getName() != null){
            sql.append(" AND t.name LIKE ?");
            params.add(filter.getName());
        }if (filter.getGameid() != null){
            sql.append(" AND t.gameid = ?");
            params.add(filter.getGameid());
        }if (filter.getElo() != null){
            sql.append(" AND t.elo = ?");
            params.add(filter.getElo());
        }if (filter.getFormat() != null){
            sql.append(" AND t.format = ?");
            params.add(filter.getFormat());
        }if (filter.getStructure() != null){
            sql.append(" AND t.structure = ?");
            params.add(filter.getStructure());
        }if (filter.getStartdate() != null){
            sql.append(" AND t.startdate < ?");
            params.add(filter.getStartdate());
        }if (filter.getEnddate() != null){
            sql.append(" AND t.enddate < ?");
            params.add(filter.getEnddate());
        }if (filter.getStructure() != null){
            sql.append(" AND t.structure = ?");
            params.add(filter.getStructure());
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), ROW_MAPPER);
    }

    @Override
    public Tournament create(Long creatorid, String name, Long gameid, Region region, Elo elo, LocalDate startdate, LocalDate enddate, String format, Structure structure, Integer max_participants) {
//        final Map<String, Object> values = new HashMap<>();
//        values.put("creatorid", creatorid);
//        values.put("name", name);
//        values.put("gameid", gameid);
//        values.put("region", region.toString());
//        values.put("elo", elo.toString());
//        values.put("startdate", startdate);
//        values.put("enddate", enddate);
//        values.put("format", format);
//        values.put("structure", structure.toString());
//        values.put("max_participants", max_participants);
//        
//        final Number tournamentId = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(values));
//        
//        return new Tournament(tournamentId.longValue(), creatorid, name, gameid, region, elo, startdate, enddate, format, structure, max_participants);
    	
    	SqlParameterSource values = new MapSqlParameterSource()
                .addValue("creatorid", creatorid)
                .addValue("name", name)
                .addValue("gameid", gameid)
                .addValue("region", region, Types.OTHER)
                .addValue("elo", elo, Types.OTHER)
                .addValue("startdate", startdate)
                .addValue("enddate", enddate)
                .addValue("format", format)
                .addValue("structure", structure, Types.OTHER)
                .addValue("max_participants", max_participants);

        Number key = jdbcInsert.executeAndReturnKey(values);
        return new Tournament(key.longValue(), creatorid, name, gameid, region, elo, startdate, enddate, format, structure, max_participants);
    }

    @Override
    public void joinTournamentUser(Long user_id, Long tournament_id) {
    	Tournament t = findById(tournament_id).orElse(null);
    	List<User> participants = getTournamentParticipants(tournament_id);
    	if (t != null && participants.size() < t.getMax_participants()) {
    		List<Pair<Integer, Integer>> firstMatches = t.firstMatches(participants.size());
    		
    		for (Pair<Integer, Integer> match : firstMatches) {
    			if(match.getRight() == 0) {
    				jdbcTemplate.update("UPDATE match SET local_id = ? WHERE id = ? AND tournament_id = ?", user_id, match.getLeft(), tournament_id);
    			}else {
					jdbcTemplate.update("UPDATE match SET visitor_id = ? WHERE id = ? AND tournament_id = ?", user_id, match.getLeft(), tournament_id);
				}
    		}
    		
	        Map<String, Object> values = new HashMap<>();
	        
	        values.put("user_id", user_id);
	        values.put("tournament_id", tournament_id);
	
	        jfbcInsertParticipant.execute(values);
    	}
    }
    
	@Override
    public void loadScores(Long match_id, Long tournament_id, Integer local_score, Integer visitor_score) {
		jdbcTemplate.update("UPDATE match SET local_score = ?, visitor_score = ? WHERE id = ? AND tournament_id = ?", local_score, visitor_score, match_id, tournament_id);
	}

    /*
    @Override
    public void joinTournamentTeam(Long team_id, Long tournament_id) {
        Map<String, Object> values = new HashMap<>();

        values.put("team_id", team_id);
        values.put("tournament_id", tournament_id);

        jfbcInsertParticipant.execute(values);
    }
    */
    
    @Override
    public List<User> getTournamentParticipants(Long tournament_id){
        return jdbcTemplate.query("SELECT u.id, u.username FROM users u" +
                                "JOIN participant_user p ON u.id = p.user_id" +
                                "WHERE p.tournament_id = ?", ROW_MAPPER_USER ,tournament_id);
    }
    
    @Override
    public Optional<Structure> getTournamentStructure(Long tournament_id) {
    	Tournament t = findById(tournament_id).orElse(null);
    	if (t != null) {
			return Optional.of(t.getStructure());
		}
    	return Optional.empty();
	}
//    
//    public Integer getTournamentMaxParticipants(Long tournament_id) {
//		Tournament t = findById(tournament_id).orElse(null);
//		if (t != null) {
//			
//		}
//    }
    
    @Override
    public void createMatches(Long tournament_id) {
		Tournament t = findById(tournament_id).orElse(null);
		if (t != null) {
			int rounds = t.amountOfMatches();
			for(int i = 0; i < rounds; i++) {
				Map<String, Object> values = new HashMap<>();
				values.put("id", i+1);
				values.put("tournament_id", tournament_id);
				values.put("local_id", null);
				values.put("visitor_id", null);
				values.put("local_score", null);
				values.put("visitor_score", null);
				jfbcInsertMatch.execute(values);
			}
		}
	}
}
