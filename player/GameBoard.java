/* GameBoard.java */

package player;

import java.util.Arrays;


public class GameBoard{

    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int SIZE = 8;
    private Chip[][] board; 				// an array board that holds objects chips 

	/**
	 * GameBoard() is a constructor that creates an empty 8*8 game board.
	 * The chips are represented by Chip objects.
	 * Empty cells are represented by null.
	 */
	public GameBoard(){
	    this.board = new Chip[SIZE][SIZE];
	}

	/**
	 * GameBoard() is a constructor that constructs a new GameBoard that's a copy of b
	 * @param b is the gameboard to be copied
	 */
	public GameBoard(GameBoard b){
	    this.board = b.board;
	}

	/**
	 * addChip() calls isValidMove and adds a Chip object which has color "color" to (x, y) on GameBoard
	 * if the cell (x, y) is empty.
	 * @param x is the x coordinate of the cell
	 * @param y is the y coordinate of the cell
	 * @param color is the color of the Chip that we are adding
	 */
	public void addChip(int x, int y, int color){
		//if (isValidMove(new Move(x, y), color)){
			board[x][y] = new Chip(color, x, y, this);
		//}
	}


	/** removeChip() removes the chip at position (x, y) from "this" GameBoard.
	* @param x is the x coordinate of the chip to be removed
	* @param y is the y coordinate of the chip to be removed
	*/
	public void removeChip(int x, int y){
		board[x][y] = null;
	}


	/**
	 * cellContent returns the content of the cell (x, y)
	 * @param x is the x coordinate of the cell
	 * @param y is the y coordinate of the cell
	 * @return a Chip object if there is one in the cell
	 *         null if the cell is empty or the cell (x, y) is not on the boards
	 */
	public Chip cellContent(int x, int y){
		if (x < 0 || x > 7 || y < 0 || y > 7){
			return null;
		}else{
			return board[x][y];
		}
	}


	/**
	 * conductMove() returns a new GameBoard object after Move m is conducted by player side if Move m
	 * by player side is valid.
	 * @param m is the Move object to be conduct
	 * @param side is the side of the player conducting the move
	 * @return a GameBoard object after the move if the Move is valid
	 *         a GameBoard the same as the one before otherwise
	 */
    void conductMove(Move m, int side){
		if (isValidMove(m, side)){
			if (m.moveKind == Move.ADD){
			      addChip(m.x1, m.y1, side);
			}

			if (m.moveKind == Move.STEP){
				removeChip(m.x2, m.y2);
				addChip(m.x1, m.y1, side);
			}
		}
	}

	void undoMove(Move m, int side){
		if (m.moveKind == Move.ADD){
		    removeChip(m.x1, m.y1);
		}

		if (m.moveKind == Move.STEP){
		    removeChip(m.x1, m.y1);
		    addChip(m.x2, m.y2, side);
		}
	}

	/**
	* isValidMove() determine whether Move m is a valid move by player "side" in the context of "this" GameBoard. 
	* A legal move is defined in the project "readme" file.
	* @param m is move that will be checked
	* @param side is the color of the player (black or white) 
	* @return true if m is a valid move in the context;
	*         false otherwise.
	*/
	public boolean isValidMove(Move m, int side){
		if (m == null){
			return false;
		}
		if (m.moveKind == Move.ADD){
		    if (cellContent(m.x1, m.y1) != null){               //if cell is already occupied
				return false;
			}

			if ((m.x1 == 0 || m.x1 == 7) && (m.y1 == 0 || m.y1 == 7)){		//in the four corners
				return false;
			}

			if (clusterIndentifier(m.x1, m.y1, side)){        // violates rule #4
				return false;
			}

			if ((side == WHITE && (m.y1 == 0 || m.y1 == 7)) || (side == BLACK && (m.x1 == 0 || m.x1 == 7))){ //opponent goal areas 
				return false;
			}

			/*										//can't have more that two chips in a goal
			if ((numIn0Goal(BLACK) > 1 && m.y1 == 0) || (numIn7Goal(BLACK) > 1 && m.y1 == 7)){
				return false;
			}
								
			if ((numIn0Goal(WHITE) > 1 && m.x1 == 0) || (numIn7Goal(WHITE) > 1 && m.x1 == 7)){
				return false;
			}
			*/
			
			if (countChips(side) == 10 && m.moveKind == Move.ADD){
				return false;
			}
			return true;
		}else{											//moveKind = STEP
			removeChip(m.x2, m.y2);
			boolean result = isValidMove(new Move(m.x1, m.y1), side);
			addChip(m.x2, m.y2, side);
			return result;			
		}
    }


	/**
	 * clusterIndentifier() determines whether adding a Chip of color "color" to
	 * (x1, y1) will make a cluster. A cluster is defined as having 3+ chips of same color with 1 adjacent to the other two.
	 * This is a helper function of isValidMove().
	 * It checks whether the adding satisfies rule #4.
	 * @param x is the x coordinate of the position to add the Chip
	 * @param y is the y coordinate of the position to add the Chip
	 * @param color is the color of Chips that we want to add
	 * @return true if the the adding make a cluster
	 *		   false otherwise
	 */
	private boolean clusterIndentifier(int x1, int y1, int color){
		int counter = 0;
		for (int x = x1 - 1; x < x1 + 2; x++){
			for (int y = y1 - 1; y < y1 + 2; y++){
				if (cellContent(x, y) != null && cellContent(x, y).color == color){
					if (x != x1 || y != y1){
						counter ++;
						if (searchNeighbour(x, y, color) > 0){
							return true;
						}
					}
				}
			}
		}

		if (counter > 1){
			return true;
		}else{
			return false;
		}
	}

	private int searchNeighbour(int x, int y, int color){
		int counter = 0;
		for (int i = x - 1; i < x + 2; i++){
			for (int j = y - 1; j < y + 2; j++){
				if (cellContent(i, j) != null && cellContent(i, j).color == color){
					if (i != x || j != y){
						counter ++; 
					}
				/*	System.out.println(i);
					System.out.println("x is "+ x);
					System.out.println(j);
					System.out.println("y is " + y);
					System.out.println(counter);  */
				}
			}
		}
		return counter;
	}

	/**
	 * listValidMoves() return an array of Move objects that represent all valid moves for player "side" in the the
	 * context of this GameBoard.
	 * @param side is MachinePlayer.COMPUTER or MachinePlayer.OPPONENT
	 * @return an array of all valid moves for player "side" in the context of "this" GameBoard.
	 */
	public Move[] listValidMoves(int side){
		if (countChips(side) < 10){
			return listValidAddMoves(side);
		}else{
			return listValidStepMoves(side);
		}
	}


	/**
	 * listValidAddMoves() return an array of Move objects that represent all valid add moves for player "side" in the
	 * context of "this"
	 * GameBoard.
	 * @param side is MachinePlayer.COMPUTER or MachinePlayer.OPPONENT
	 * @return an array of all valid add moves for player "side" in the context of "this" GameBoard.
	 */
	private Move[] listValidAddMoves(int side){
		Move[] addList = new Move[100];
		int addListCount = 0;
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				Move addMove = new Move(x, y);
				if(isValidMove(addMove, side)) {
					addList[addListCount] = addMove;
					addListCount++;
				}
			}
		}
		return addList;
	}

	/**
	 * listValidStepMoves() return an array of Move objects that represent all valid step moves for player "side" in the context of "this"
	 * GameBoard.
	 * @param side is MachinePlayer.COMPUTER or MachinePlayer.OPPONENT
	 * @return an array of all valid step moves for player "side" in the context of "this" GameBoard.
	 */
	private Move[] listValidStepMoves(int side){
		Move[] stepList = new Move[1000];
		int stepListCount = 0;
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int xNew = 0; xNew < SIZE; xNew++) {
					for (int yNew = 0; yNew < SIZE; yNew++) {
						if (cellContent(x, y) != null && cellContent(x,y).color == side && xNew != x && yNew != y) {
							Move stepMove = new Move(xNew, yNew, x, y);
							if(isValidMove(stepMove, side)) {
								stepList[stepListCount] = stepMove;
								stepListCount++;
							}
						}
					}
				}
			}
		}
		return stepList;
	}






	/**
	 * nwIdentify() determines whether there's a winning network for player "side".
	 * @param side is MachinePlayer.COMPUTER or MachinePlayer.OPPONENT
	 * @return true if there is a network for player "side"
	 *         false otherwise
	 */
  	public boolean nwIdentify(int side){
		boolean return_value = false;
		Chip[] in_goal = new Chip[6];
		if (side == BLACK){
			int k = 0;
			for (int x = 1; x < 7; x++){
				if (this.cellContent(x, 0) != null && this.cellContent(x, 0).color == BLACK){
					in_goal[k] = this.cellContent(x, 0);
					k++; 
				}
			}
		}else{
			int k = 0;
			for (int y = 1; y < 7; y++){
				if (this.cellContent(0, y) != null && this.cellContent(0, y).color == BLACK){
					in_goal[k] = this.cellContent(0, y);
					k++; 
				}
			}			
		}


		for (Chip cp :in_goal){
			if (cp != null){
				if (cp.numConnection() == 0){
					continue;
				}else{
					Chip[] connection = cp.findConnection();
					for (Chip connected : connection){
					    if (connected != null){
						Chip[] prev_chips = new Chip[10];
						prev_chips[0] = cp;
						prev_chips[1] = connected;
						return_value = return_value || nwHelper(side, 2, connected, cp.direction(connected),prev_chips);
					    }
					}
				}
			}
		}
		return return_value;
	}

	private boolean nwHelper(int side, int numchips, Chip currchip, int[] direction, Chip[] prevlist){
	    boolean result = false;
		Chip[] connection = currchip.findConnection();
		for (Chip cp : connection){
		    if (cp != null && notInArray(cp, prevlist)){
		    	if ((side == BLACK && cp.y == 0)||(side == WHITE && cp.x == 0)){
		    		return false;											//cannot pass the goal twice
		    	}
		    	if (numchips < 5 && ((side == BLACK && cp.y == 7) || (side == WHITE && cp.x == 7))){
		    		continue;												//cannot pass the goal twice before reaching a network
		    	}
				if (Arrays.equals(currchip.direction(cp), direction)){
				    continue;												//must change direction
				}else if (numchips >= 5 && ((side == BLACK && cp.y == 7) || (side == WHITE && cp.x == 7))){
				    return true;											//NETWORK!!!
				}else{														//recursive call
				    prevlist[numchips] = cp;
				    result = result || nwHelper(side, numchips +1, cp, currchip.direction(cp), prevlist);
				    prevlist[numchips] = null;
			    }
		    }
		}
		return result;
	}

    private boolean notInArray(Object o, Object[] array){
	for (Object elem:array){
	    if (elem == o){
		return false;
	    }
	}
	return true;
    }


    Chip[] listChips(int side){
    	Chip[] list = new Chip[10];
    	int k = 0;
    	for (int x = 0; x < SIZE ;x++){
    		for (int y = 0; y < SIZE; y++){
    			if (cellContent(x, y) != null && cellContent(x, y).color == side){
    				list[k] = cellContent(x, y);
    				k++;
    			}
    		}
    	}
    	return list;
    }

    int countChips(int side){
    	int num = 0;
    	Chip[] list = this.listChips(side);
    	for (Chip elem: list){
    		if (elem != null){
    			num++;
    		}
    	}
    	return num;
    }



    int totalConnection(int side){
    	int num = 0;
        for (int x = 0; x < SIZE; x++){
		    for (int y = 0; y < SIZE; y++) {
				Chip currChip = this.cellContent(x, y);
		        if (currChip != null && currChip.color == side) {
				    num += MachinePlayer.square(currChip.numConnection());
		        }
		    }
		}
		return num/2;		      	
    }


	int numIn0Goal(int side) {
		int counter = 0;
		if (side == GameBoard.BLACK){
			for (int x = 1; x < GameBoard.SIZE - 1; x++){
				if (cellContent(x, 0) != null){
					counter++;
				}
			}
		}else{
			for (int y = 1; y < GameBoard.SIZE - 1; y++){
				if (cellContent(0, y) != null){
					counter++;
				}	
			}			
		}
		return counter;
	}

	int numIn7Goal(int side){
		int counter = 0;
		if (side == GameBoard.BLACK){
			for (int x = 1; x < GameBoard.SIZE - 1; x++){
				if (cellContent(x, 7) != null){
					counter++;
				}
			}
		}else{
			for (int y = 1; y < GameBoard.SIZE - 1; y++){
				if (cellContent(7, y) != null){
					counter++;
				}	
			}			
		}
		return counter;
	}

    /**
     * hashCode() gives this gameBoard a hashCode.
     */
    public int hashCode(){
		int hashVal = 0;
		for (int x = 0; x < SIZE; x++){
		    for (int y = 0; y < SIZE; y++){
			hashVal = (3 * hashVal + hashHelper(cellContent(x, y))) % 16908799;
		    }
		}
		return hashVal;
    }

    private int hashHelper(Chip c){
		if (c == null){
		    return 2;
		}else{
		    return c.color;
		}
    }



	public static void main(String[] args){
		GameBoard test = new GameBoard();
		MachinePlayer player = new MachinePlayer(WHITE,3);
		Move m = new Move(7,4);
		test.addChip(7,6,WHITE);
		test.addChip(5,4,WHITE);
		test.addChip(6,6,WHITE);
		test.addChip(6,4,WHITE);
		test.addChip(7,2,WHITE);
		test.addChip(6,1,WHITE);
		test.addChip(3,4,WHITE);
		test.addChip(4,6,WHITE);
		test.addChip(3,6,WHITE);
		test.addChip(3,3,WHITE);
		
		test.addChip(4,4,BLACK);
		test.addChip(2,3,BLACK);
		test.addChip(6,5,BLACK);
		test.addChip(5,3,BLACK);
		test.addChip(2,4,BLACK);
		test.addChip(6,0,BLACK);
		test.addChip(5,7,BLACK);
		test.addChip(5,1,BLACK);
		test.addChip(2,0,BLACK);
		test.addChip(3,7,BLACK);
		
		
		System.out.println(Arrays.toString(test.listValidMoves(WHITE)));
	/*	test.addChip(5,7,BLACK);
		test.addChip(3,3,BLACK);
		test.addChip(3,5,BLACK);
		test.addChip(3,4,WHITE);
		test.addChip(6,5,WHITE);
		test.addChip(3,1,WHITE);
		test.addChip(1,2,WHITE);
		test.addChip(6,6,WHITE);*/
		//player.board = test;
		System.out.println(player.chooseMove());
		
	}
}
