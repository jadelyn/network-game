/* Chip.java */

package player;

/**
 *  The Chip class defines an object that models a chip used by either
 *  the Black player or the White player.
 *  See the README file accompanying this project for additional details.
 */

public class Chip{

	public int color;
	public int oppcolor;
	public int x;
	public int y;
	public GameBoard myboard;

	/**
	* Chip() is a constructor that creates a chip with 
	* color (black or white) color, and a position (x,y).
	*/
	public Chip(int color, int x, int y, GameBoard board){
		this.color = color;
		if (color == GameBoard.BLACK){
			this.oppcolor = GameBoard.WHITE;
		}else{
			this.oppcolor = GameBoard.BLACK;
		}
		this.x = x;
		this.y = y;
		this.myboard = board;
	}

	public int[] direction(Chip cp){
		int x = cp.x - this.x;
		int y = cp.y - this.y;
		if (x > 0){
			x = 1;
		}else if(x < 0){
			x = -1;
		}

		if (y > 0){
			y = 1;
		}else if(y < 0){
			y = -1;
		}
		int[] rt = {x, y};
		return rt;		
	}


	/**
	* findConnection() return a list of chips that are connected to this chip.
	* @return an array of chips that is connected to this chip.
	*/
	public Chip[] findConnection(){
		int k = 0;
		Chip[] list =  new Chip[8];
		if (color != GameBoard.BLACK || (y != 0 && y != 7)){
			for (int x = this.x - 1; x >= 0; x--){
				if (myboard.cellContent(x, this.y) != null && myboard.cellContent(x, this.y).color == this.color){
					list[k] = myboard.cellContent(x, this.y);
					k++;
					break;
				}else if(myboard.cellContent(x, this.y) != null && myboard.cellContent(x, this.y).color != this.color){
					break;
				}
			}
			for (int x = this.x + 1; x < 8; x++){
				if (myboard.cellContent(x, this.y) != null && myboard.cellContent(x, this.y).color == this.color){
					list[k] = myboard.cellContent(x, this.y);
					k++;
					break;
				}else if(myboard.cellContent(x, this.y) != null && myboard.cellContent(x, this.y).color != this.color){
					break;
				}
			}			
		}	

		if (color != GameBoard.WHITE || (x != 0 && x != 7)){	
			for (int y = this.y + 1; y < 8; y++){
				if (myboard.cellContent(this.x, y) != null && myboard.cellContent(this.x, y).color == this.color){
					list[k] = myboard.cellContent(this.x,y);
					k++;
					break;
				}else if(myboard.cellContent(this.x, y) != null && myboard.cellContent(this.x, y).color != this.color){
					break;
				}
			}
			for (int y = this.y - 1; y >= 0; y--){
				if (myboard.cellContent(this.x,y) != null && myboard.cellContent(this.x,y).color == this.color){
					list[k] = myboard.cellContent(this.x,y);
					k++;
					break;
				}else if(myboard.cellContent(this.x, y) != null && myboard.cellContent(this.x, y).color != this.color){
					break;
				}
			}
		}

		int x = this.x - 1;
		int y = this.y - 1;
		while (x >= 0 && y >= 0){
			if (myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color == this.color){
				list[k] = myboard.cellContent(x, y);
				k++;
				break;
			}else if(myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color != this.color){
				break;
			}else{
				x--;
				y--;
			}
		}
		x = this.x + 1;
		y = this.y - 1;
		while (x < 8 && y >= 0){
			if (myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color == this.color){
				list[k] = myboard.cellContent(x, y);
				k++;
				break;
			}else if(myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color != this.color){
				break;
			}else{
				x++;
				y--;
			}
		}
		x = this.x - 1;
		y = this.y + 1;
		while (x >= 0 && y < 8){
			if (myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color == this.color){
				list[k] = myboard.cellContent(x, y);
				k++;
				break;
			}else if(myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color != this.color){
				break;
			}else{
				x--;
				y++;
			}
		}
		x = this.x + 1;
		y = this.y + 1;
		while (x < 8 && y < 8){
			if (myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color == this.color){
				list[k] = myboard.cellContent(x, y);
				k++;
				break;
			}else if(myboard.cellContent(x, y)!= null && myboard.cellContent(x, y).color != this.color){
				break;
			}else{
				x++;
				y++;
			}
		}				
		return list;		
	}	

	/**
	 * numConnection() returns the number of connections this Chip has.
	 */
	public int numConnection(){
		int k = 0;
		int num = 0;
		Chip[] list = findConnection();
		while(k < 8){
			if(list[k] == null){
				break;
			}else{
				k++;
				num++;
			}
		}
		return num;
	}



	public Chip[] inGoal() {
		Chip[] in_goal = new Chip[2];
		if (color == GameBoard.BLACK){
			int k = 0;
			for (int x = 1; x < 7; x++){
				if (myboard.cellContent(x, 0) != null && myboard.cellContent(x, 0).color == GameBoard.BLACK){
					in_goal[k] = myboard.cellContent(x, 0);
					k++; 
				}
			}
		}else{
			int k = 0;
			for (int y = 1; y < 7; y++){
				if (myboard.cellContent(0, y) != null && myboard.cellContent(0, y).color == GameBoard.WHITE){
					in_goal[k] = myboard.cellContent(0, y);
					k++; 
				}
			}			
		}
		return in_goal;
	}


	/**
	 * numBlocked return the number of opponent's connection that this chip blocks, helper function of the evaluation function
	 * @return number of opponent's connection that this chip blocks
	 */
	public int numBlocked(){
		int num_with = myboard.totalConnection(oppcolor);
		myboard.removeChip(this.x, this.y);
		int num_without = myboard.totalConnection(oppcolor);
		myboard.addChip(this.x, this.y, this.color);
		return num_without - num_with;
	}

	public String toString(){
		return "Chip of color " + color + " in position (" + x + "," + y +")"; 
	}
}
