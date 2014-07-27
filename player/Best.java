package player; 

public class Best {
	Move move;
	double score;
	public Best(){

	}
	
	public Best(Move m, double s){
		this.move = m;
		this.score = s;
	}
	
	public String toString(){
		return "move:  " + move + ", score: " + score;
	}
}