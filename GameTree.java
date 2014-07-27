package Game;

import List.*;
import player.*;

class bestMove {
	Move move;
	double score;

	bestMove(Move m, double num) {
		move = m;
		score = num;
	}

	bestMove() {

	}
	
	public String toString() {
		return "Move: " + move + " Score: " + score;
	}
}

public class GameTree {
	// Remember, in MachinePlayer, black = 0, and white = 1, but we'll most
	// likely increment that value when passing it so it works with the final
	// variables below
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;

	// Performs an alpha beta game tree search, for current Board configuration
	// b, and for player turn player. It searches as deep as the given depth and
	// returns the best Move based on the minmax algorithm for successive turns.
	// Returns the best move as searched

	public static Move search(GameBoard b, int player, int depth) {
		bestMove temp = chooseMove(b, depth, Integer.MIN_VALUE, Integer.MAX_VALUE,
				player, player);
		return temp.move;
	}
	
	//The recursive method that does the alpha beta game tree search. Our depth number goes down, where the final iteration is at depth = 0
	//Returns the best move it finds (as stored in bestMove)
	private static bestMove chooseMove(GameBoard b, int depth, double alpha,
			double beta, int player, int currentPlayer) {

		bestMove myBest = new bestMove();
		bestMove reply;
		int winner = GameState.findWinner(b);
		if (winner == player) {
			return new bestMove(new Move(), 100 / (10 - depth));
		}
		if (winner == oppColor(player)) {
			return new bestMove(new Move(), -100 / (10 - depth));
		}
		if (winner == 3) {
			if (currentPlayer == player) {
				return new bestMove(new Move(), 100 / (10 - depth));
			} else {
				return new bestMove(new Move(), -100 / (10 - depth));
			}
		}
		if (depth == 0) {
			if (currentPlayer == player) {
				return new bestMove(new Move(), evaluate(b, currentPlayer));
			} else {
				return new bestMove(new Move(), -evaluate(b, currentPlayer));
			}
		}
		if (currentPlayer == player) {
			myBest.score = alpha;
		} else {
			myBest.score = beta;
		}
		List moveList = GameState.generateList(b, currentPlayer);
		ListNode tempNode = moveList.front();
		while (tempNode.isValidNode()) {
			GameBoard tempBoard = new GameBoard(b);
			Move tempMove = (Move) tempNode.item();
			tempBoard.makeMove(tempMove, currentPlayer);
			reply = chooseMove(tempBoard, depth - 1, alpha, beta, player,
					oppColor(currentPlayer));
			if ((currentPlayer == player) && (reply.score > myBest.score)) {
				myBest.move = tempMove;
				myBest.score = reply.score;
				alpha = reply.score;
			} else if ((currentPlayer != player)
					&& (reply.score < myBest.score)) {
				myBest.move = tempMove;
				myBest.score = reply.score;
				beta = reply.score;
			}
			if (alpha >= beta) {
				return myBest;
			}
			tempNode = tempNode.next();
		}
		return myBest;
	}


	// Evaluates the board for the player (player is passed through the
	// integer), returns the "value" of the board, for use in a Game Tree Search
	private static double evaluate(GameBoard b, int player) {
		double human = 0.0, ai = 0.0;
		int tempLenYou = 0, tempLenOpFor = 0;
		List friendly = b.getPlayerPieces(player);
		List foe = b.getPlayerPieces(player % 2 + 1); // 1 maps to 2 and 2 maps
														// to 1 in this way
		DList you = (DList) friendly;
		DList opfor = (DList) foe;
		ListNode tempYou = you.front();
		ListNode tempOpFor = opfor.front();
		int lenYou = you.length();
		int lenOpFor = opfor.length();
		while (tempYou.isValidNode() || tempOpFor.isValidNode()) {
			Position tempOne = (Position) tempYou.item();
			Position tempTwo = (Position) tempOpFor.item();
			if (tempOpFor != opfor.back().next() && tempLenOpFor < lenOpFor) {
				ai += GameState.findConnections(tempTwo, b).length();
			}
			if (tempYou != you.back().next() && tempLenYou < lenYou) {
				human += GameState.findConnections(tempOne, b).length();
			}
			tempYou = tempYou.next();
			tempOpFor = tempOpFor.next();
			tempLenYou++;
			tempLenOpFor++;
		}
		return (human - ai / 2) / 100;
	}
	
	//Returns the opposite player's color
	private static int oppColor(int color) {
		return (color % 2 + 1);
	}

	public static void main(String args[]) {
		GameBoard herp = new GameBoard();
		herp.makeMove(new Move(3, 3), BLACK);
		herp.makeMove(new Move(3, 4), BLACK);
		//herp.makeMove(new Move(2, 3), BLACK);
		herp.makeMove(new Move(5, 5), BLACK);
		herp.makeMove(new Move(5, 6), BLACK);
		herp.makeMove(new Move(6, 3), BLACK);
		herp.makeMove(new Move(0, 3), WHITE);
		herp.makeMove(new Move(0, 5), WHITE);
		herp.makeMove(new Move(2, 3), WHITE);
		herp.makeMove(new Move(2, 4), WHITE);
		herp.makeMove(new Move(4, 6), WHITE);
		System.out.println(herp);
		System.out.println(search(herp, WHITE, 3));
		//System.out.println(search(herp, BLACK, 3));
		System.out.println(GameState.hasCompletedNetwork(herp, BLACK));

		// herp.makeMove(new Move(5,6), WHITE);

		/*System.out.println(herp);
		System.out.println(search(herp, BLACK, 3));
		herp.makeMove(search(herp, BLACK, 3), BLACK);
		System.out.println(herp);

		System.out.println(search(herp, WHITE, 3));
		herp.makeMove(search(herp, WHITE, 3), WHITE);
		System.out.println(herp);
		System.out.println(search(herp, BLACK, 3));
		herp.makeMove(search(herp, BLACK, 3), BLACK);
		System.out.println(herp);
		System.out.println(search(herp, WHITE, 3));
		herp.makeMove(search(herp, WHITE, 3), WHITE);
		System.out.println(herp);
		System.out.println(search(herp, BLACK, 3));
		herp.makeMove(search(herp, BLACK, 3), BLACK);
		System.out.println(search(herp, WHITE, 3));
		herp.makeMove(search(herp, WHITE, 3), WHITE);
		System.out.println(herp);*/

	}

}
