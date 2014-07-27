package Game;
import List.*;
import player.*;


public class GameState {
	// Remember, in MachinePlayer, black = 0, and white = 1, but we'll most
	// likely increment that value when passing it so it works with the final
	// variables below
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;

	// Checks and returns whether move m is valid on Board configuration
	public static boolean isValid(Move m, GameBoard b, int color) {
		// No move
		GameBoard tempBoard = new GameBoard(b);
		if (m.moveKind == Move.QUIT) {
			return true;
		}
		// Addition move
		else if (m.moveKind == Move.ADD && b.getPlayerPieces(color).length() < 10) {
			Position tempPosition = new Position(m.x1, m.y1);
			if (m.x1 < 0 || m.x1 > 7 || m.y1 < 0 || m.y1 > 7) {
				return false;
			} else if (isCorner(tempPosition)) {
				return false;
			} else if (isOppositeGoal(tempPosition, color)) {
				return false;
			} else if (isOccupied(tempPosition, b)) {
				return false;
			} else {
				tempBoard.setPiece(tempPosition,color);
				return validBoard(tempBoard);
			}
		}
		else if (m.moveKind == Move.STEP) {
			Position tempPosition = new Position(m.x1, m.y1);
			if (m.x2 < 0 || m.x2 > 7 || m.y2 < 0 || m.y2 > 7) {
				return false;
			} else if (isCorner(tempPosition)) {
				return false;
			} else if (isOppositeGoal(tempPosition, color)) {
				return false;
			} else if (isOccupied(tempPosition, b)) {
				return false;
			} else if (tempBoard.getPiece(new Position(m.x2, m.y2)) != color) {
				return false;
			} else if (m.x1 == m.x2 && m.y1 == m.y2){
				return false;
			} else {
				tempBoard.setPiece(new Position(m.x2, m.y2), EMPTY);
				tempBoard.setPiece(tempPosition, color);
				
				return validBoard(tempBoard);
			}
		} 
		return true;
	}

	// Checks if Position p is occupied on GameBoard b
	private static boolean isOccupied(Position p, GameBoard b) {
		if (b.getPiece(p) != EMPTY) {
			return true;
		} else {
			return false;
		}
	}

	// Checks if the position and color are in the wrong goal
	private static boolean isOppositeGoal(Position p, int color) {
		if (color == BLACK) {
			if (p.x == 0 || p.x == 7) {
				return true;
			} else {
				return false;
			}
		} else {
			if (p.y == 0 || p.y == 7) {
				return true;
			} else {
				return false;
			}
		}
	}

	// Returns whether the Position is in the corner or not
	private static boolean isCorner(Position p) {
		int x = p.x;
		int y = p.y;
		if ((x == 0 && y == 0) || (x == 7 && y == 0) || (x == 0 && y == 7)
				|| (x == 7 && y == 7)) {
			return true;
		} else {
			return false;
		}
	}

	// Checks whether the board configuration is valid, checks for whether we have any groups of 3 conjoined pieces
	private static boolean validBoard(GameBoard b) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				int[] neighbor = neighbors(b, i, j);
				if (neighbor[neighbor[0]] > 1) { // Only checks neighbors of the same color								
					return false;
				}
			}
		}
		return true;
	}
	
	//Returns an integer array of rhe neighbors of the piece at position (x, y)
	private static int[] neighbors(GameBoard b, int x, int y) {
		int[] neighbors = new int[3]; // Index 0 represents the color of the
										// piece at position (x,y); Index 1
										// represents the black neighbors and
										// Index 2 represents the white
										// neighbors
		int piece = b.getPiece(new Position(x, y));
		neighbors[0] = piece;
		if(piece != EMPTY){
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (x + i < 0 || y + j < 0 || x + i > 7 || y + j > 7 || b.getPiece(new Position(x + i, y + j)) == EMPTY) {
						continue;
					} 
					else if (i == 0 && j == 0){
						continue;
					}
					else {
						neighbors[b.getPiece(new Position(x + i, y + j))]++;
					}
				}
			}
		}
		return neighbors;
	}

	// Checks every possible move on the board, if the move is valid, add it to
	// the list, then return the list
	public static List generateList(GameBoard b, int color) {
		List validMoves = new DList();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (color == BLACK) {
					if (b.getBlackPieces().length() < 10) {
						Move tempMove = new Move(i, j);
						if (isValid(tempMove, b, BLACK)) {
							validMoves.insertBack(tempMove);
						}
					} else {
						ListNode temp = b.getBlackPieces().front();
						for (int a = 0; a < 10; a++) {
							Position tempPos = (Position) temp.item();
							Move tempMove = new Move(i, j, tempPos.get_x(),
									tempPos.get_y());
							if (isValid(tempMove, b, BLACK)) {
								validMoves.insertBack(tempMove);
							}
						}
					}
				} else if (color == WHITE) {
					if (b.getWhitePieces().length() < 10) {
						Move tempMove = new Move(i, j);
						if (isValid(tempMove, b, WHITE)) {

							validMoves.insertBack(tempMove);
						}
					} else {
						ListNode temp = b.getWhitePieces().front();
						for (int a = 0; a < 10; a++) {
							Position tempPos = (Position) temp.item();
							Move tempMove = new Move(i, j, tempPos.get_x(),
									tempPos.get_y());
							if (isValid(tempMove, b, WHITE)) {
								validMoves.insertBack(tempMove);
							}
						}
					}
				}
			}
		}
		return validMoves;

	}

	// Returns a list of pieces(positions) of the same color that are connected
	// to the piece at Position p, (first node of returned list might contain
	// information like the position of the piece we searched from initially
	public static List findConnections(Position p, GameBoard b) {
		List result = new DList();
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				if (x == 0 && y == 0) {
					continue;
				} else {
					Position temp = findPiece(p, b, x, y);
					if (temp != null) {
						result.insertBack(temp);
					}
				}
			}
		}
		return result;
	}

	/*
	 * Finds a piece in the specified direction (Remember, up and to the left is
	 * negative, down and to the right is positive Returns null if it finds an
	 * opponents piece or if it just finds nothing
	 */
	private static Position findPiece(Position p, GameBoard b, int xChange,
			int yChange) {
		int color = b.getPiece(p);
		Position temp = new Position(p.x, p.y);
		temp.x += xChange;
		temp.y += yChange;
		while (temp.x > -1 && temp.x < 8 && temp.y > -1 && temp.y < 8) {
			if (b.getPiece(temp) == EMPTY) {
				temp.x += xChange;
				temp.y += yChange;
			} else if (b.getPiece(temp) == color) {
				return temp;
			} else {
				return null;
			}
		}
		return null;
	}

	// Returns true if at least one player has a winning configuration,
	// otherwise return false. Is the initial call for the recursive function searchNetwork. 
	// If the function returns an exception, then there ix a complete network.
	public static boolean hasCompletedNetwork(GameBoard b, int player) {
		List searchPieces = getGoalPieces(b, player);
		try {
			ListNode tempNode = searchPieces.front();
			while (tempNode.isValidNode()) {
				searchNetwork(b, (Position) tempNode.item(), new DList(),
						player);
				tempNode = tempNode.next();
			}
		} catch (NetworkException e) {
			return true;
		}
		return false;
	}
	
	//Returns a list of pieces in the starting goal in GameBoard b for player
	private static List getGoalPieces(GameBoard b, int player) {
		List playerPieces = new DList(b.getPlayerPieces(player));
		ListNode tempNode = playerPieces.front();
		while (tempNode.isValidNode()) {
			if (isStartingGoal((Position) tempNode.item())) {
				tempNode = tempNode.next();
				continue;
			} else {
				tempNode = tempNode.next();
				tempNode.prev().remove();
			}
		}
		return playerPieces;
	}

	// Recursive function that checks each individual node as part of a network.
	// It checks if the current List including that piece creates
	// a full network. If a full network is created, a NetworkException is called, which exits the recursion. Otherwise it
	// continues iterating until there are no options left
	private static void searchNetwork(GameBoard b, Position p, List previous,
			int player) throws NetworkException {
		// Check for winner first
		if (isWinningGoal(p)) {
			if(previous.length() >= 5) {
				previous.insertBack(p);
				throw new NetworkException(previous);
			}
			else {
				return;
			}
		}
		// If not winner, generate list of connection positions
		List connectedPieces = findConnections(p, b);
		if (connectedPieces.length() == 0) {
			previous.insertBack(p);
			return;
		}
		ListNode connectedHead = connectedPieces.front();
		ListNode previousPosition = previous.back();
		if (previous.length() == 0) {
			while (connectedHead.isValidNode()) {
				Position tempPosition = (Position) connectedHead.item();
				if (isStartingGoal(tempPosition)) {
					connectedHead = connectedHead.next();
					connectedHead.prev().remove();
					continue;
				}
				connectedHead = connectedHead.next();
			}
		} else {
			int lastAngle = getSlope(p, (Position) previousPosition.item());
			while (connectedHead.isValidNode() && previous.length() > 0) {
				Position tempPosition = (Position) connectedHead.item();
				// Filter list for same angle
				if (getSlope(tempPosition, p) == lastAngle) {
					connectedHead = connectedHead.next();
					connectedHead.prev().remove();
					continue;
				}
				// Filter list for previous positions used
				if (previous.contains(tempPosition) || tempPosition.equals(p)) {
					connectedHead = connectedHead.next();
					connectedHead.prev().remove();
					continue;
				}
				// Filter list for goal pieces
				if (isStartingGoal(tempPosition)) {
					connectedHead = connectedHead.next();
					connectedHead.prev().remove();
					continue;
				}
				connectedHead = connectedHead.next();
			}
		}
		// If list is empty, then don't return anything
		if (connectedPieces.length() == 0) {
			previous.insertBack(p);
			return;
		}
		// Apply recursion to every remaining position
		else {
			ListNode tempNode = connectedPieces.front();
			while (tempNode.isValidNode()) {
				List tempList = new DList(previous);
				tempList.insertBack(p);
				searchNetwork(b, (Position) tempNode.item(), tempList, player);
				tempNode = tempNode.next();
			}
			return;
		}
	}

	// Gets the slope between Position p and Position previous. Returns 0 for
	// vertical, 1 for positive diagonal, -1 for negative diagonal, and 2 for
	// horizontal
	private static int getSlope(Position p, Position previous) {
		int xSlope = p.x - previous.x;
		int ySlope = p.y - previous.y;
		if (xSlope == 0) {
			return 2;
		} else if (ySlope == 0) {
			return 0;
		} else if (xSlope * ySlope > 0) {
			return 1;
		} else {
			return -1;
		}
	}

	// Checks if the piece is on the bottom or right side goal (Ending goals)
	private static boolean isWinningGoal(Position p) {
		if (p.x == 7 || p.y == 7) {
			return true;
		} else {
			return false;
		}
	}

	// Checks if the piece is in the top or left side goals (Starting goals)
	private static boolean isStartingGoal(Position p) {
		if (p.x == 0 || p.y == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Returns 1 if Black wins, 2 if White wins, and 3 if both are winners
	// (Return values subject to change) (Return 0 if no winner, but this method
	// shouldn't be called unless hasCompletedNetwork returns true)
	public static int findWinner(GameBoard b) {
		boolean blackWin, whiteWin;
		blackWin = hasCompletedNetwork(b, BLACK);
		whiteWin = hasCompletedNetwork(b, WHITE);
		if(blackWin) {
			if(whiteWin) {
				return 3;
			}
			else {
				return BLACK;
			}
		}
		else {
			if(whiteWin) {
				return WHITE;
			}
			else {
				return 0;
			}
		}
	}

	public static void main(String args[]) {
		GameBoard herp = new GameBoard();
		herp.makeMove(new Move(1, 0), BLACK);
		herp.makeMove(new Move(0, 1), WHITE);
		herp.makeMove(new Move(1, 2), BLACK);
		herp.makeMove(new Move(0, 3), WHITE);
		herp.makeMove(new Move(0, 5), WHITE);
		herp.makeMove(new Move(1, 4), BLACK);
		herp.makeMove(new Move(2, 1), WHITE);
		herp.makeMove(new Move(3, 0), BLACK);
		herp.makeMove(new Move(2, 3), WHITE);
		herp.makeMove(new Move(3, 2), BLACK);
		herp.makeMove(new Move(2, 5), WHITE);
		herp.makeMove(new Move(3, 4), BLACK);
		herp.makeMove(new Move(4, 1), WHITE);
		herp.makeMove(new Move(5, 0), BLACK);
		herp.makeMove(new Move(4, 3), WHITE);
		herp.makeMove(new Move(5, 2), BLACK);
		herp.makeMove(new Move(4, 5), WHITE);
		herp.makeMove(new Move(6, 5), WHITE);
		herp.makeMove(new Move(5, 4), BLACK);
		herp.makeMove(new Move(5, 6), BLACK);
		
		System.out.println(herp);
		for(int a=0; a<8;a++){
			for(int b=0; b<8;b++){
				for(int c=0; c<8;c++){
					for(int d=0; d<8; d++){
						if(isValid(new Move(a,b,c,d), herp, WHITE)){
							
							herp.makeMove(new Move(a,b,c,d), WHITE);
						}
						if(isValid(new Move(a,b,c,d), herp, BLACK)){
							
							herp.makeMove(new Move(a,b,c,d), BLACK);
						}
					}
				}
			}
		}
		

		
		
		System.out.println(herp);

	}

}
