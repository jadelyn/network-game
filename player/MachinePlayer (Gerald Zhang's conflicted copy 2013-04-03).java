
/* MachinePlayer.java */

package player;
import dict.*;
import java.util.Arrays;

/**
 *  An implementation of an automatic Network player. Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
  int color;
  int oppcolor;
  private GameBoard board;
  private int depth;
  private HashTableChained board_dict;



  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
    this.board = new GameBoard();
    this.depth = 2;
    if (color == GameBoard.BLACK){
    	oppcolor = GameBoard.WHITE;
    }else{
    	oppcolor = GameBoard.BLACK;
    }
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    this(color);
    this.depth = searchDepth;
  }
  
	/**
	 * evaluate() returns a score for GameBoard state. The score indicates the winning chance for both players. 
	 * 500 is a winning score for
	 * "this" player, and -500 is a winning score for the opponent.
	 * @param state is the current GameBoard state
	 * @param opponent is the opponent MachinePlayer
	 * @return a score indicating how well "this" MachinePlayer is doing.
	 */
	public double evaluate(GameBoard state){
		double score = 0;
	    if (state.nwIdentify(this.color)){	// if win for "this" MachinePlayer
			score = 500.0;
	    }
	    else if (state.nwIdentify(oppcolor)){	// if win for opponent
			score = -500.0;
	    }else{                                 //neither player completed a network
	       	int numchips = state.countChips(color);               		//number of chips this player has
	       	int numoppchips = state.countChips(oppcolor); 				//number of chips the opponent has
 	       	int chipConn = state.totalConnection(color);				//count the connections of this player(weighted by squaring)
	       	int oppChipConn = state.totalConnection(oppcolor);			//count the connections of the opponent(weighted by squaring)
	       	Chip[] chiplist = state.listChips(this.color);				//a list of Chips of this player
	       	Chip[] oppchiplist = state.listChips(oppcolor);				//a list of Chips of opponent

	       	score += 30 * (chipConn - oppChipConn);     //modify the score by the different of this player's connection with opponent's connection
	       	
			if (numchips + numoppchips < 6){               //put chips in goals early
				if (state.numIn0Goal(color) + state.numIn7Goal(color)  > 0){
					score += 10;
				}
				if(state.numIn0Goal(oppcolor) + state.numIn7Goal(oppcolor) > 0){
					score -= 10;
				}
			}
			for (Chip c : chiplist){
				if (c != null && hasNeighbor(c)){		//if c has a neighbor, the connection is unblockable, modify score accordingly
					score +=  10;
				}
			}

			for (Chip c : oppchiplist){
				if (c != null && hasNeighbor(c)){
					score -= 10;
				}
			}


			for (Chip c : chiplist){
				if (c != null){
					score += 10 * square(c.numBlocked());        //check how many connection each chip block, and change the score accordingly
				}
			}
			for (Chip c : chiplist){
				if (c != null){
					score -= 10 * square(c.numBlocked());       
				}
			}
		}
		return score;
	}

	/**
	* square() is a helper function that returns the square of x.
	* @param x is the number to be squared
	* @return the square of x.
	*/
	private int square(int x) {
		return x * x;
	}

	/**
	* hasNeighbor() is a helper function that determines whether "chip" has a neighbor, 
	* which is another chip either right next to "chip" or connected to
	* it diagonally without any spaces in between.
	* @param chip is the chip that is checked for neighbors
	* @return true if "chip" does have a neighbor; returns false otherwise.
	*/
	private boolean hasNeighbor(Chip chip) {
		for (int x = chip.x - 1; x < chip.x + 2; x++){
			for (int y = chip.y - 1; y < chip.y + 2; y++){
				if (chip.myboard.cellContent(x, y) != null && chip.myboard.cellContent(x, y).color == chip.color && (chip.x != x || chip.y != y)){
					return true;
				}
			}
		}
		return false;
	}



  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Move m = chooseMove(color, -500, 500, depth).move;
    board.conductMove(m, color);
    if (board.isValidMove(m,color){
	    return m;
	    System.out.println("sound good to me");
	}else{
	    return new Move(3,4);
  } 





  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    if (board.isValidMove(m, oppcolor)){
	board.conductMove(m, oppcolor);
        return true;
    }else{
    	return false;
    }
  }



  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
      if (board.isValidMove(m, color)){
	        board.conductMove(m, color);
	  	return true;
      }else{
	  	return false;
      } 
  }

  /**
   * chooseMove() returns the Move that yields that highest winning chance by "this" player
   * and internally records move and updates internal game board as move by "this"  
   * an array of Move object, using game tree search and alpha beta pruning 
   * and at maximum search depty calls evaluate(). 
   * @param moves is the array of valid Moves to search from
   * @return the Best object that yields the highest winning chance 
   **/

	  public Best chooseMove(int side, int alpha, int beta, int searchdepth){
	      int oppside;
	      if (side == GameBoard.BLACK){
	    	  oppside = GameBoard.WHITE;
	      }else{
	    	  oppside = GameBoard.BLACK;
	      }
	      Move[] valid_moves = board.listValidMoves(side);
	      // System.out.println(Arrays.toString(valid_moves));
	       
	      Best myBest = new Best(); //new Best(valid_moves[0], evaluate(board.conductMove(color)));   //comp best move
	      Best reply;  	 //opponent's best move
	      
	      if(evaluate(board) == 500.0 || searchdepth == 0){
	      	return new Best(null, evaluate(board));     //guaranteed a win or reached search depth
	      }
	 
	      if (side == color) {
		  	myBest.score = alpha;
	      } else {
		  	myBest.score = beta;   	//side is opponent 
	      }

	      for (Move m : valid_moves){
	    	  if (m != null){
		      	board.conductMove(m, side);
		      	reply = chooseMove(oppside, alpha, beta, searchdepth - 1);
		      	board.undoMove(m, side);
		      	
		      	if (side == color && reply.score >= myBest.score){
		      		myBest.move = m;
		      		myBest.score = reply.score;
		      	} else if (side == oppcolor && reply.score <= myBest.score){
		      		myBest.move = m;
		      		myBest.score = reply.score;
		      	}
		      	
		      	if (alpha >= beta){
		      		return myBest;
		      	}
	    	  }else{
		      break;
		  }
		  
	      }
	      return myBest;
	  }
	  
	  

	  
}







