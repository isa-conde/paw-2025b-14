package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Tournament.TournamentNode;

import java.util.List;

public class Group extends TournamentNode {
	private int teams;
	private List<Long> teamList;
	
	public Group(int teams, List<Long> teamList) {
		this.teams = teams;
		this.teamList = teamList;
	}
	
	@Override
	public int getDepth() {
		return 0;
	}
	
	@Override
	public List<Long> getTeamIds() {
		return teamList;
	}
	
	@Override
	public int getSize() {
		return teams;
	}
}
