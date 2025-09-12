package ar.edu.itba.paw.model.Tournament;

public class TournamentGraph {
	private TournamentNode finalNode;
	
	public TournamentGraph(TournamentNode finalNode) {
		this.finalNode = finalNode;
	}
	
	public int getDepth() {
		return finalNode.getDepth();
	}
	
	public TournamentNode getFinalNode() {
		return finalNode;
	}
}
