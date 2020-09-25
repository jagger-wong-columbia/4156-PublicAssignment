package models;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  private int[] rows;

  private int[] cols;
  
  private int diagonal;
  
  private int antidiagonal;
  
  private int count;
  
  /** Initialize method of the board.
   */
  // Initialize game;
  public void initialize() {
    winner = 0;
    isDraw = false;
    this.rows = new int[3];
    this.cols = new int[3];
    this.diagonal = 0;
    this.antidiagonal = 0;
    count = 0;
  }
  
  /** Start method of the board.
   */
  // Start game;
  public void startGame(char type) {
    this.p1 = new Player();
    this.p1.initPlayer(type, 1);
    this.boardState = new char[3][3];
    turn = 1;
  }
  
  /** Join method of the board.
   */
  // Player2 join;
  public void joinPlayer() {
    this.p2 = new Player();
    char type = this.p1.getType();
    if (type == 'O') {
      this.p2.initPlayer('X', 2);
    } else {
      this.p2.initPlayer('O', 2);
    }
    this.gameStarted = true;
  }
  
  // get current turn;
  public int getTurn() {
    return this.turn;
  }
  
  /** Move method of the board.
   */
  // Make a move;
  public boolean makeMove(int x, int y, int playerId) {
    // check if this move is valid;
    if (this.boardState[x][y] == 'O' || this.boardState[x][y] == 'X') {
      return false;
    } else {
      ++count;
      if (playerId == 1) {
        this.boardState[x][y] = this.p1.getType();
        this.rows[x] += 1;
        this.cols[y] += 1;
        if (x == y) {
          this.diagonal += 1;
        }
        if (x + y == 2) {
          this.antidiagonal += 1;
        }
      } else {
        this.boardState[x][y] = this.p2.getType();
        this.rows[x] -= 1;
        this.cols[y] -= 1;
        if (x == y) {
          this.diagonal -= 1;
        }
        if (x + y == 2) {
          this.antidiagonal -= 1;
        }
      }
      return true;
    }
  }
  
  // switch turn;
  public void setTurn(int playerId) {
    this.turn = playerId;
  }
  
  /** Check method of the board.
   */
  // check if one player win or if two player draw;
  public boolean checkWin(int playerId, int x, int y) {
    if (Math.abs(this.rows[x]) == 3 || Math.abs(this.cols[y]) == 3
        || Math.abs(this.diagonal) == 3 || Math.abs(this.antidiagonal) == 3) {
      this.gameStarted = false;
      this.winner = playerId;
      return true;
    }
    if (count == 9) {
      this.isDraw = true;
    }
    return false;
  }
}
