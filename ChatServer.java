import java.io.*;
import java.net.*;
import java.time.Instant;
import java.util.*;

public class ChatServer {

	protected int serverPort = 1234;
	protected HashMap<Socket, String> clients = new HashMap<>();

	public static void main(String[] args) throws Exception {
		new ChatServer();
	}

	public ChatServer() {
		ServerSocket serverSocket = null;

		// create socket
		try {
			serverSocket = new ServerSocket(this.serverPort); // create the ServerSocket
		} catch (Exception e) {
			System.err.println("[system] could not create socket on port " + this.serverPort);
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// start listening for new connections
		System.out.println("######  ChatServer  ######");
		System.out.println("[system] listening ...");
		try {
			while (true) {
				Socket newClientSocket = serverSocket.accept(); // wait for a new client connection
				synchronized (this) {
					clients.put(newClientSocket, ""); // add client to the list of clients
				}
				ChatServerConnector conn = new ChatServerConnector(this, newClientSocket); // create a new thread for communication with the new client
				conn.start(); // run the new thread
			}
		} catch (Exception e) {
			System.err.println("[error] Accept failed.");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		// close socket
		System.out.println("[system] closing server socket ...");
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	// send a message to all clients connected to the server
	public void sendToAllClients(String message) throws Exception {
		for (Socket socket : clients.keySet()) { // iterate through the client list
			try {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages to the client
				out.writeUTF(message); // send message to the client
			} catch (Exception e) {
				System.err.println("[system] could not send message to a client");
				e.printStackTrace(System.err);
			}
		}
	}

	public void sendToClient(String sender, String name, String message) throws Exception {
		Socket socket = null;
		for (Socket s : this.clients.keySet()) {
			if (this.clients.get(s).equals(name)) {
				socket = s;
			}
		}

		if (socket == null) {
			Instant instant = Instant.now();
			String time = "" + instant.getEpochSecond();
			String[] msg = {"s", time, " user " + name + " does not exist"};
			sendToClient("server", sender, EncoderDecoder.encode(msg));
		}
		try {
			assert socket != null;
			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages to the client
			out.writeUTF(message); // send message to the client
		} catch (Exception e) {
			System.err.println("[system] user not found");
			e.printStackTrace(System.err);
		}
	}


	public void removeClient(Socket socket) {
		synchronized (this) {
			clients.remove(socket);
		}
	}
}

class ChatServerConnector extends Thread {
	private final ChatServer server;
	private final Socket socket;

	public ChatServerConnector(ChatServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public void run() {
		System.out.println("[system] connected with " + this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort());

		DataInputStream in;
		try {
			in = new DataInputStream(this.socket.getInputStream()); // create input stream for listening for incoming messages
		} catch (IOException e) {
			System.err.println("[system] could not open input stream!");
			e.printStackTrace(System.err);
			this.server.removeClient(socket);
			return;
		}

		while (true) { // infinite loop in which this thread waits for incoming messages and processes them
			String msg_received;
			try {
				msg_received = in.readUTF(); // read the message from the client
			} catch (Exception e) {
				System.err.println("[system] there was a problem while reading message client on port " + this.socket.getPort() + ", removing client");
				e.printStackTrace(System.err);
				this.server.removeClient(this.socket);
				return;
			}
			if (msg_received.isEmpty()) // invalid message
				continue;

			String[] msg = EncoderDecoder.decode(msg_received);
			System.out.println("[" + server.clients.get(this.socket) + "] : " + Arrays.toString(EncoderDecoder.decode(msg_received))); // print the incoming message in the console


			try {
				switch (msg[0]) {
					case "j":
						this.server.sendToAllClients(msg_received); // send message to all clients
						break;
					case "z":
						this.server.sendToClient(msg[2], msg[3], msg_received); // send message to all clients
						break;
					case "a":
						server.clients.put(this.socket, msg[2]);
						break;
				}
			} catch (Exception e) {
				System.err.println("[system] there was a problem while sending the message to all clients");
				e.printStackTrace(System.err);
			}
		}
	}
}
