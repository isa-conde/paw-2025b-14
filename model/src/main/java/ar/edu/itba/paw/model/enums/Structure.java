package ar.edu.itba.paw.model.enums;

import ar.edu.itba.paw.model.Pair;

import java.util.ArrayList;
import java.util.List;

public enum Structure {
    LEAGUE{
    	@Override
		public List<Pair<String, String>> buildMatches(int maxParticipants) {
    		if(maxParticipants < 2) return null;
    		List<Pair<String, String>> matches = new ArrayList<>();
    		buildMatchesRec(matches, maxParticipants-1);
			return matches;
		}
    	
    	private void buildMatchesRec(List<Pair<String,String>> prev, int remaining) {
			if(remaining == 0) return;
			for(int i = 0; i < remaining; i++) {
				prev.add(new Pair<>(String.valueOf(remaining), String.valueOf(i)));
			}
			buildMatchesRec(prev, remaining-1);
		}
    }, ELIMINATION{
    	@Override
		public List<Pair<String, String>> buildMatches(int maxParticipants) {
    		if(maxParticipants < 2 || !isPowerOfTwo(maxParticipants)) return null;
			List<Pair<String, String>> matches = new ArrayList<>();
			for(int i = 0; i < maxParticipants/2; i++) {
				matches.add(new Pair<>(String.valueOf(i), String.valueOf(maxParticipants - 1 - i)));
			}
			buildMatchesRec(matches, maxParticipants/2, 0);
			return matches;
    	}
    	
    	private void buildMatchesRec(List<Pair<String,String>> prev, int remaining, int stages) {
			if(remaining < 2) {
				return;
			}
			for(int i = 0; i < remaining/2; i++) {
				prev.add(new Pair<>("W"+String.valueOf(i*2+stages), "W"+String.valueOf(i*2+1+stages)));
			}
			buildMatchesRec(prev, remaining/2, stages + remaining);
    	}
    	
    	private boolean isPowerOfTwo(int n) {
			return (n & (n - 1)) == 0;
    	}
    }, HYBRID{
    	@Override
		public List<Pair<String, String>> buildMatches(int maxParticipants) {
			if(maxParticipants < 6 || maxParticipants % 2 == 1) return null;
			List<Pair<String,String>> matches = new ArrayList<Pair<String,String>>();
			int groups = factorByTwo(maxParticipants);
			if(isPowerOfTwo(maxParticipants)) {
				groups/=4;
			}
			int groupSize = maxParticipants / groups; // grupos de 11 personas en adelante tienen más de 55 partidos cada uno.
			// no recomendaría cantidades de participantes múltiplos de primos mayores a 7 o de 15, 21, 25, 27, 35, 49, 63 (consejo personal)
			buildGroupMatches(matches, groupSize, groups);
			int qualified = closestPowerOfTwo(groupSize/2);
			buildBracketMatches(matches, groups, qualified);
			return matches;
    	}
    	
    	private void buildGroupMatches(List<Pair<String,String>> matches, int groupSize, int groups) {
			buildGroupMatchesRec(matches, groupSize-1, groups, groupSize);
		}
		
		private void buildGroupMatchesRec(List<Pair<String,String>> prev, int remaining, int groups, int groupSize) {
			if(remaining == 0) return;
			for(int g = 0; g < groups; g++) {
				for(int i = 0; i < remaining; i++) {
					prev.add(new Pair<>(String.valueOf(g*groupSize + remaining), String.valueOf(g*groupSize + i)));
				}
			}
			buildGroupMatchesRec(prev, remaining-1, groups, groupSize);
		}
    	
    	private void buildBracketMatches(List<Pair<String,String>> matches, int groups, int qualified) {
    		for(int i=0; i < groups; i++) {
    			for(int j=0; j < qualified/2; j++) {
					matches.add(new Pair<>(String.valueOf((char)('A'+i)) + String.valueOf(j), String.valueOf((char)('A'+((i+1)%groups)))+String.valueOf(qualified-1-j)));
				}
    		}
			buildMatchesRec(matches, qualified*groups/4, groups);
    	}
    	
    	private void buildMatchesRec(List<Pair<String,String>> prev, int remaining, int groups) {
    		if(remaining == 0) return;
    		if(remaining <= 2) {
				// acá es inevitable que se crucen equipos del mismo grupo, así que se hace simple
    			for(int i=0; i<remaining; i++) {
					prev.add(new Pair<>("W"+String.valueOf(i), "W"+String.valueOf(remaining + i)));
    			}
    			buildMatchesRec(prev, remaining/2, groups);
				return;
    		}
    		for(int i=0; i<remaining; i++) {
				prev.add(new Pair<>("W"+String.valueOf(i), "W"+String.valueOf(remaining*2 - ( i/groups + 1 )*groups + ( i + groups/2 )%groups ))); 
				// la idea es que a los que mejor les fue en la fase de grupos les toque jugar contra los que peor les fue y que hasta semis no tengan chances de cruzarse a alguien de su grupo
    		}
			buildMatchesRec(prev, remaining/2, groups);
    	}
    	
    	private boolean isPowerOfTwo(int n) {
			return (n & (n - 1)) == 0;
    	}
    	
    	private int factorByTwo(int n) {
			int count = 0;
			while(n % 2 == 0) {
				n /= 2;
				count++;
			}
			return count;
    	}
    	
    	private int closestPowerOfTwo(int n) {
			int power = 1;
			while(power*2 <= n) {
				power *= 2;
			}
			return power;
    	}
    };
    
    public abstract List<Pair<String,String>> buildMatches(int maxParticipants);
}
