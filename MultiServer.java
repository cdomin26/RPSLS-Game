package src.project4;

/*
Jhon Nunez, Christian Dominguez, Joseph Canning
University of Illinois
CS342 Software Design
Project 4: Rock, Paper, Scissors, Lizard, Spock(Multiplayer Mode)
*/

//package p3_server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;



public class MultiServer extends Application {

    public static final String WEL_SIG = "welcome";           // client has connected to server
    public static final String WAIT_SIG = "wait";             // client is the first to connect to server
    public static final String TURN_SIG = "your move";        // client's turn
    public static final String PLAY_SIG = "client played";    // client made a play; server needs to read/process it
    public static final String WIN_SIG = "win";               // client won round/game
    public static final String LOSE_SIG = "lose";             // client lost round/game
    public static final String TIE_SIG = "tie";               // both clients tied
    public static final String OP_SIG = "op:";                // opponent's score
    public static final String END_SIG = "over";              // game is over
    public static final String YES_SIG = "yes:";              // client wants rematch
    public static final String NO_SIG = "no:";                // client doesn't want rematch
    public static final String FORCE_SIG = "force:";          // force client disconnect
    public static final String UPDATE_SIG = "update list"; 	  // update list of clients 
    public static final String CLEAR_SIG = "clear list";	  // clears list of clients	
    public static final String CHALLENGE_SIG = "challenge:"; // challenge an opponent from our list
    public static final String REJECT_SIG = "reject"; 			//reject challenge
    public static final String DISABLE_SIG = "disable";		  //disable ability to challenge another client 	
	
    
    //private Scanner in;             // reads input
    //private PrintWriter out;        // writes output
    //private Socket client;          // client this listener is responsible for
    //private volatile String last;   // last signal received from client

    enum Play { // enum values to be used as signals for plays

        ROCK,
        PAPER,
        SCISSORS,
        LIZARD,
        SPOCK

    }
    
    
    
// reference types
private ServerSocket server;						// connects clients
private HashMap<Integer, InputListener> listeners; 	// IO for connected clients; keyed by client ID
private Stage myStage; 								// primary stage
private Scene host; 								// host scene
private Button on,off,setPort;
private TextField port;
private Label clientNumber; //p1Score,p2Score,p1Status,p2Status, winner;
//private ArrayList<String> clientPlays;
private ArrayList<Integer> clientList;
//private HashMap<Integer,Integer> playing;

Label gameTitle;





// primitives
private int portNumber;
//private int plays; 									// amount of times played
//private int numPlayers; 

//private int p1ScoreVal = 0; 						// player 1 score
//private int p2ScoreVal = 0; 						// player 2 score
//private int replay = 0;								// replay
private int nextID;									// next client ID to be assigned


public static void main(String[] args) throws IOException{
	launch(args);	
}

@Override
public void start(Stage primaryStage) {

	myStage = primaryStage; // create primary stage
	primaryStage.setTitle("RPSLS Host By: Team 13");
	//HOST SCENE---------------------------------------------------------------------------------------
	gameTitle = new Label("Welcome to RPSLS! Enter Port Number!");
	gameTitle.setFont(new Font("Arial",12) );
	Label clients = new Label("Number of Clients");
	clientNumber = new Label("0");
	clientNumber.setFont(new Font("Arial", 12));
	clientNumber.setStyle("-fx-font-weight: bold");
	clientNumber.setTextFill(Color.DARKRED);
	
	clientList = new ArrayList<Integer>();
	
	BorderPane gamePane = new BorderPane(); //create pane for card game
	gamePane.setPadding(new Insets(50)); //add padding
	//gamePane.setBackground(new Background(gameBI));//set background image
	
	on = new Button("Turn On");
	on.setMaxWidth(200);
	off = new Button("Turn Off");
	off.setMaxWidth(200);
	setPort = new Button("Create");
	setPort.setMaxWidth(200);
	port = new TextField();
	port.setPromptText("Enter Port Number to listen to");
	port.setMaxWidth(200);	
	port.setText("7777");
	
	HBox top = new HBox(gameTitle); //title
	top.setAlignment(Pos.TOP_CENTER);

	VBox center = new VBox(port,setPort, on, off,clients,clientNumber);
	center.setAlignment(Pos.TOP_CENTER);
	
	gamePane.setTop(top);
	gamePane.setCenter(center);
	
	host = new Scene(gamePane, 340,250);//create scene for host
	primaryStage.setScene(host); //set Scene
	primaryStage.show(); //show Scene
	
	//DISABLE ON and OFF
	on.setDisable(true);
	off.setDisable(true);

	
	setPort.setOnAction(e -> { // assign portNumber to value entered by user
		try {
			portNumber = Integer.parseInt(port.getText());
			on.setDisable(false);
			setPort.setDisable(true);
			port.setDisable(true);
			System.out.println("Port set!");
		} catch (NumberFormatException x) {
			x.printStackTrace();
			portNumber = 0;
		}
	});
	
	off.setOnAction(e -> { // disconnect the server when off is pressed
		disconnect();
		setPort.setDisable(false);
		off.setDisable(true);
		on.setDisable(false);
	});
	
	on.setOnAction(e -> { // start the server
		acceptClients();
		setPort.setDisable(true);
		on.setDisable(true);
		off.setDisable(false);
	});

} // end of start

private void disconnect() { // close all client connections; disable server

//	clientNumber = 0; // 
//	Platform.runLater(() -> numClientsLabel.setText(CLIENT_TEXT + numClients)); // 

	for (InputListener l : listeners.values()) {
		Socket s = l.getClient();
		try {
			l.send(FORCE_SIG + -1); // send client force disconnect signal with server tag
			s.close();
		} catch (Exception x) {
			x.printStackTrace();

			if (x instanceof IOException) {
				System.err.println("\nPlayer already disconnected\n");
			} else {
				System.err.println("\nNo player to disconnect\n");
			}
		}
	}
	try {
		server.close();
	} catch (Exception x) {
		x.printStackTrace();
		if (x instanceof IOException) {
			System.err.println("\nServer could not be disconnected\n");
		} else {
			System.err.println("\nUnknown error\n");
		}
	}

}

@Override
public void init() {

	//numPlayers = 0;
	//plays = 0;
	//clientPlays = new ArrayList<>();

}

@Override
public void stop() {

	disconnect();
	System.exit(0);

}

// creates a new thread to that runs forever, accepting any clients that wish to join the server; this method should be run immediately after the server starts
private synchronized void acceptClients() {

	new Thread(() -> { // wait for players to connect

		try {

			listeners = new HashMap<>();
			server = new ServerSocket(portNumber);
			InputListener clientIO;

			while (true) {

				listeners.put(nextID, new InputListener(server.accept())); // accept client connection, establish IO
				clientIO = listeners.get(listeners.size() - 1); // get the client that was just connected
				clientIO.start(); // start IO thread
				clientIO.send(WEL_SIG + ++nextID);

				Platform.runLater(() -> clientNumber.setText(""+nextID)  ); //update number of clients on server
				Platform.runLater(() -> clientNumber.setTextFill(Color.GREEN)  );
				
				clientList.add(nextID); //add list 
				updateList(); //update list to all clients 

				//listen(server);
//				Platform.runLater(() -> numClientsLabel.setText(CLIENT_TEXT + ++numClients)); // 

			}
		} catch (IOException x) {
			x.printStackTrace();
			disconnect(); // tear down server
			System.err.println("\nFailed to set up server\n");

		} finally {
			System.gc();
		}
	}).start();

}



public void updateList() { //update list of all clients 
	for (InputListener l : listeners.values()) {
		try {
			l.send( CLEAR_SIG ); // clear client's previous list
			for(int i =0; i < clientList.size(); i++) {
				l.send(UPDATE_SIG + clientList.get(i)); // send client update list command
			}
		} catch (Exception x) {x.printStackTrace();}
	}
}

private void startGame(InputListener one, InputListener two) {


	new Thread(() -> {

		Play x, y;
		
		one.send(TURN_SIG);
		two.send(DISABLE_SIG); 
		
        while (!one.last.equals(PLAY_SIG)) {}
        while (one.last.equals(PLAY_SIG)) {} //wait for next signal for number 
        
		x = intToPlay(Integer.parseInt(one.last));
		
        two.send(TURN_SIG);
        while (!two.last.equals(PLAY_SIG)) {}
        while (two.last.equals(PLAY_SIG)) {} //wait for next signal for number 
        y = intToPlay(Integer.parseInt(two.last));
        System.out.println(""+two.last);
       
        int z = comparePlays(x,y);
        System.out.println(""+z);
        
        if (z == 0) {
        	one.send(WIN_SIG);
        	two.send(LOSE_SIG);
        }
        if (z == 1) {
        	two.send(WIN_SIG);
        	one.send(LOSE_SIG);
        }
        if (z == -1) {
        	one.send(TIE_SIG);
        	two.send(TIE_SIG);
        }     
        
        //send players their opponents play
        two.send(OP_SIG + x.toString());
        one.send(OP_SIG + y.toString());
        
	}).start();

}

public Play intToPlay(int play) { // converts int representation of a play to an enum type

    switch (play) {

        case 0  : return Play.ROCK;
        case 1  : return Play.PAPER;
        case 2  : return Play.SCISSORS;
        case 3  : return Play.LIZARD;
        case 4  : return Play.SPOCK;
        default  : return null;

    }

}

// returns 0 if player 1's play wins, 1 if player 2's play wins, or -1 if they match
private int comparePlays(Play one, Play two) { //one -> player 1, two -> player 2

	if (one == Play.ROCK) {

		if (two == Play.PAPER) {
			return 1;
		} else if (two == Play.SCISSORS) {
			return 0;
		} else if (two == Play.LIZARD) {
			return 0;
		} else if (two == Play.SPOCK) {
			return 1;
		} else {
			return -1;
		}

	} else if (one == Play.PAPER) {

		if (two == Play.ROCK) {
			return 0;
		} else if (two == Play.SCISSORS) {
			return 1;
		} else if (two == Play.LIZARD) {
			return 1;
		} else if (two == Play.SPOCK) {
			return 0;
		} else {
			return -1;
		}

	} else if (one == Play.SCISSORS) {

		if (two == Play.ROCK) {
			return 1;
		} else if (two == Play.PAPER) {
			return 0;
		} else if (two == Play.LIZARD) {
			return 0;
		} else if (two == Play.SPOCK) {
			return 1;
		} else {
			return -1;
		}

	} else if (one == Play.LIZARD) {

		if (two == Play.ROCK) {
			return 1;
		} else if (two == Play.PAPER) {
			return 0;
		} else if (two == Play.SCISSORS) {
			return 1;
		} else if (two == Play.SPOCK) {
			return 0;
		} else {
			return -1;
		}

	} else { // one == spock

		if (two == Play.ROCK) {
			return 0;
		} else if (two == Play.PAPER) {
			return 1;
		} else if (two == Play.SCISSORS) {
			return 0;
		} else if (two == Play.LIZARD) {
			return 1;
		} else {
			return -1;
		}

	}

}

public class InputListener extends Thread {

	private Scanner in;             // reads input
	private PrintWriter out;        // writes output
	private Socket client;          // client this listener is responsible for
	private volatile String last;   // last signal received from client

    public InputListener(Socket client) { // constructor initializes in and out

        this.client = client;

        try {
            in = new Scanner(client.getInputStream());
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {{
        	
        }
            e.printStackTrace();
            System.err.println("\nUnable to read from client IO streams");
        }
    }

    @Override
    public void run() {

        // if statements check for signals and execute appropriate responses; String.contains() should be used if an ID may follow the signal, otherwise use String.equals()
        while (in.hasNextLine()) {
            last = in.nextLine();
            if (last.contains(NO_SIG)) { // client has quit the game; close their Socket
//              Platform.runLater(() -> numClientsLabel.setText(CLIENT_TEXT + --numClients)); 
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (last.contains(CHALLENGE_SIG)) {
            	
            	String ids = last.substring(CHALLENGE_SIG.length()); // for last = "challenge:100:101," get "100:101"
                String[] idsSplit =  ids.split(":"); // split ids into a new substring around each colon and newline character
                int id1 = Integer.parseInt(idsSplit[0]); // first substring is the first id
                int id2 = Integer.parseInt(idsSplit[1]); // second substring is the second id
                
            	
            	InputListener a = listeners.get(id1-1);
            	InputListener b = listeners.get(id2-1);
            	
            	//if(id2 != playing.get(id2) ) {//not in hashmap
            	
            	
            	Platform.runLater(() -> gameTitle.setText("Player "+""+id1  +" Challenges Player "+id2) );//test
            	//add players to playing hashmap 
            	//playing.put(id1,id1);
            	//playing.put(id2,id2);
            	startGame(a,b);	//start game for two clients
            	//}
            	//else {
            		
            		//a.send(REJECT_SIG);
            		
            	//}
            }
            
        }
        // close IO once Socket is closed
        in.close();
        out.close();
    }

    public void send(String message) { // sends message to client's input stream
        out.println(message);
        out.flush();
    }

    // getters/setters
    public Socket getClient() { return client; }

}



}

