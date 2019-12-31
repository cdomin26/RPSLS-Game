package src.project4;

/*
 *  InputListener.java
 *
 *  Author(s): Joseph Canning (jec2)
 *
 *  Description:
 *
 *      This class is to be used by the server to listen for input from clients connected to the server and respond to
 *      that input when appropriate. I/O is in the form of Strings written by a PrintWriter and read by a Scanner.
 *      All String signals are public, static, and final, so they can be freely accessed by all classes but not
 *      modified, as these signals should be identical to the server and the client. Reading and writing to a client
 *      is done inside a thread.
 */



import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class InputListener extends Thread {
//
//    // TODO: some of these signals may no longer be useful; add/subtract as necessary; refactor any modifications
//    // signals (string constants)
//    public static final String WEL_SIG = "welcome";           // client has connected to server
//    public static final String WAIT_SIG = "wait";             // client is the first to connect to server
//    public static final String TURN_SIG = "your move";        // client's turn
//    public static final String PLAY_SIG = "client played";    // client made a play; server needs to read/process it
//    public static final String WIN_SIG = "win";               // client won round/game
//    public static final String LOSE_SIG = "lose";             // client lost round/game
//    public static final String TIE_SIG = "tie";               // both clients tied
//    public static final String OP_SIG = "op:";                // opponent's score
//    public static final String END_SIG = "over";              // game is over
//    public static final String YES_SIG = "yes:";              // client wants rematch
//    public static final String NO_SIG = "no:";                // client doesn't want rematch
//    public static final String FORCE_SIG = "force:";          // force client disconnect
//    public static final String UPDATE_SIG = "update list"; 	  // update list of clients 
//    public static final String CLEAR_SIG = "clear list";	  // clears list of clients	
//    public static final String CHALLENGE_SIG = "chalenge player"; // challenge an opponent from our list
//
//    private Scanner in;             // reads input
//    private PrintWriter out;        // writes output
//    private Socket client;          // client this listener is responsible for
//    private volatile String last;   // last signal received from client
//
//    enum Play { // enum values to be used as signals for plays
//
//        ROCK,
//        PAPER,
//        SCISSORS,
//        LIZARD,
//        SPOCK
//
//    }
//
//    public InputListener(Socket client) { // constructor initializes in and out
//
//        this.client = client;
//
//        try {
//
//            in = new Scanner(client.getInputStream());
//            out = new PrintWriter(client.getOutputStream(), true);
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            System.err.println("\nUnable to read from client IO streams");
//
//        }
//
//    }
//
//    @Override
//    public void run() {
//
//        // if statements check for signals and execute appropriate responses; String.contains() should be used if an ID may follow the signal, otherwise use String.equals()
//        while (in.hasNextLine()) {
//
//            last = in.nextLine();
//
//            if (last.contains(NO_SIG)) { // client has quit the game; close their Socket
//
////              Platform.runLater(() -> numClientsLabel.setText(CLIENT_TEXT + --numClients)); // FIXME: update number of clients in UI
//
//                try {
//                    client.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//
//        // close IO once Socket is closed
//        in.close();
//        out.close();
//
//    }
//
//    public void send(String message) { // sends message to client's input stream
//
//        out.println(message);
//        out.flush();
//
//    }
//
//    // getters/setters
//    public Socket getClient() { return client; }
//
}

