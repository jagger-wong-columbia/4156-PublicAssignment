package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;


class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;
  
  private static GameBoard board = new GameBoard();

  /** Main method of the application.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    
    // Test Hello ASE
    app.get("/hello", ctx -> {
      ctx.result("Hello World!");
    });

    // New game;
    app.get("/newgame", ctx -> {
      board.initialize();
      ctx.redirect("/tictactoe.html");
    });
    
    // Start game;
    app.post("/startgame", ctx -> {
      Gson gson = new Gson();
      char type = ctx.body().split("=")[1].charAt(0);
      board.startGame(type);
      String json = gson.toJson(board);
      ctx.result(json);
    });

    // Join Game;
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      board.joinPlayer();
      Gson gson = new Gson();
      String json = gson.toJson(board);
      sendGameBoardToAllPlayers(json);
    });
    
    // Move;
    app.post("/move/1", ctx -> {
      if (board.getTurn() == 1) {
        String[] info = ctx.body().split("&");
        int x = Integer.valueOf(info[0].split("=")[1]);
        int y = Integer.valueOf(info[1].split("=")[1]);
        Gson gson = new Gson();
        Message m = new Message();
        
        if (board.makeMove(x, y, 1)) {
          m.confirmValid();
          ctx.result(gson.toJson(m));
          board.setTurn(2);
        } else {
          m.confirmInvalid();
          ctx.result(gson.toJson(m));
        }
        
        if (board.checkWin(1, x, y)) {
          Message win = new Message();
          win.confirmWin(1);
          ctx.result(gson.toJson(win));
        }
        
        String json = gson.toJson(board);
        sendGameBoardToAllPlayers(json);
      }
    });
    
    app.post("/move/2", ctx -> {
      if (board.getTurn() == 2) {
        String[] info = ctx.body().split("&");
        int x = Integer.valueOf(info[0].split("=")[1]);
        int y = Integer.valueOf(info[1].split("=")[1]);
        Gson gson = new Gson();
        Message m = new Message();
        
        if (board.makeMove(x, y, 2)) {
          m.confirmValid();
          ctx.result(gson.toJson(m));
          board.setTurn(1);
        } else {
          m.confirmInvalid();
          ctx.result(gson.toJson(m));
        }
        
        if (board.checkWin(2, x, y)) {
          Message win = new Message();
          win.confirmWin(1);
          ctx.result(gson.toJson(win));
        }
        
        String json = gson.toJson(board);
        sendGameBoardToAllPlayers(json);
      }
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /** Send message to all players.
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
