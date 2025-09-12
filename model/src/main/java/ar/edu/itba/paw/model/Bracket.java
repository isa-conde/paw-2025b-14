package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.Tournament.TournamentNode;

import java.util.ArrayList;
import java.util.List;

public class Bracket extends TournamentNode {
	private final TournamentNode left;
	private final TournamentNode right;
	private Long leftWinner;
	private Long rightWinner;

	public Bracket(TournamentNode left, TournamentNode right) {
		this.left = left;
		this.right = right;
	}
	
	public void setLeftWinner(Long teamId) {
		this.leftWinner = teamId;
	}
	
	public void setRightWinner(Long teamId) {
		this.rightWinner = teamId;
	}

	@Override
	public int getDepth() {
		return 1 + Math.max(left.getDepth(), right.getDepth());
	}
	
	@Override
	public List<Long> getTeamIds() {
		List<Long> teamIds = new ArrayList<>();
		teamIds.add(leftWinner);
		teamIds.add(rightWinner);
		return teamIds;
	}
	
	@Override
	public int getSize() {
		return 2;
	}
}
