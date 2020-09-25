package models;

public class Player {

  private char type;

  private int id;
  
  public void initPlayer(char type, int id) {
    this.type = type;
    this.id = id;
  }
  
  
  public char getType() {
    return this.type;
  }
}
