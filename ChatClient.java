import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;


public class ChatClient extends Thread {
	protected int serverPort = 1234;
	public static String name;

	public static void main(String[] args) throws Exception {
		new ChatClient();
	}

	public ChatClient() throws Exception {
		Socket socket = null;
		DataInputStream listenStream = null;
		DataOutputStream sendStream = null;

		System.out.println("######  ChatClient  ######");
		System.out.print("[client] set name: ");
		BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
		String uname = user_input.readLine();

		// connect to the chat server
		try {
			System.out.println("[client] connecting to chat server ...");
			socket = new Socket("localhost", this.serverPort); // create socket connection
			listenStream = new DataInputStream(socket.getInputStream()); // create input stream for listening for incoming messages
			sendStream = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages
			System.out.println("[client] connected");

			ChatClientMessageReceiver message_receiver = new ChatClientMessageReceiver(listenStream); // create a separate thread for listening to messages from the chat server
			message_receiver.start(); // run the new thread
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		name = aMessage(uname, sendStream);
		System.out.printf("[client] your name is now %s.\n", name);

		String userInput;
		//System.out.printf("\n#%s>", name);
		while ((userInput = user_input.readLine()) != null) { // read a line from the console
			if(userInput.charAt(0)=='@') {
				zMessage(userInput, sendStream);
			} else {
				jMessage(userInput, sendStream);
			}
		}

		// cleanup
		sendStream.close();
		listenStream.close();
		user_input.close();
		socket.close();
	}

	private String aMessage(String uname, DataOutputStream out){
		try {
			Instant instant = Instant.now();
			String time = "" + instant.getEpochSecond();
			String[] msg = {"a", time , uname};
			out.writeUTF(EncoderDecoder.encode(msg));
		} catch (IOException e){
			System.err.println("[system] izbor imena ni mogoč");
			e.printStackTrace(System.err);
		}
		return uname;
	}
	private void jMessage(String message, DataOutputStream out) {
		try {
			Instant instant = Instant.now();
			String time = "" + instant.getEpochSecond();
			String[] msg = { "j", time, name, message};
			out.writeUTF(EncoderDecoder.encode(msg)); // send the message to the chat server
			out.flush(); // ensure the message has been sent
		} catch (IOException e) {
			System.err.println("[system] could not send message");
			e.printStackTrace(System.err);
		}
		//System.out.printf("#%s>", name);
	}

	private void zMessage(String message, DataOutputStream out) {
		try {
			Instant instant = Instant.now();
			String time = "" + instant.getEpochSecond();

			String receiver = message;
			receiver = receiver.substring(receiver.indexOf("@") + 1);
			receiver = receiver.substring(0, receiver.indexOf(" "));

			String[] msg = {"z", time, name, receiver, message};
			out.writeUTF(EncoderDecoder.encode(msg)); // send the message to the chat server
			out.flush(); // ensure the message has been sent
		} catch (IOException e) {
			System.err.println("[system] could not send message");
			e.printStackTrace(System.err);
		}
		//System.out.printf("\n#%s>", name);
	}
}

// wait for messages from the chat server and print the out
class ChatClientMessageReceiver extends Thread {
	private final DataInputStream in;

	public ChatClientMessageReceiver(DataInputStream in) {
		this.in = in;
	}

	public void run() {
		try {
			String message;
			while ((message = this.in.readUTF()) != null) { // read new message
				displayMessage(message);
				//System.out.printf("%s>", ChatClient.name);

			}
		} catch (Exception e) {
			System.err.println("[system] could not read message");
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	public void displayMessage(String message){
		String msg_string = "";
		String[] msg = EncoderDecoder.decode(message);
		Date date = new Date(Integer.parseInt(msg[1]) * 1000L);
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		switch (msg[0]) {
			case "j":
				// "\033[0;36m" - cyan, "\033[0m" - reset
				msg_string = String.format("%s <@\033[0;36m%s\033[0m> %s", df.format(date), msg[2], msg[3]);
				break;
			case "z":
				msg_string = String.format("%s <@\033[0;36m%s\033[0m> (%s) %s", df.format(date), msg[2], "\033[0;35mzasebno\033[0m", msg[4].substring(msg[3].length()+2));
				break;
			case "s":
				msg_string = "[server] " + msg[2];
				break;
		}
		System.out.println(msg_string);

	}
}
