package ar.edu.itba.paw.model;

import java.util.List;

public abstract class TournamentNode {
	public abstract int getDepth();
	public abstract List<Long> getTeamIds();
	public abstract int getSize();
}
