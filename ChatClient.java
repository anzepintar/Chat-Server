import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;


public class ChatClient extends Thread {

    protected int serverPort = 1234;
    String passphrase = "serverpwd";

    public ChatClient() throws Exception {

        SSLSocket socket = null;
        DataInputStream listenStream = null;
        DataOutputStream sendStream = null;
        String pubcert = "";	// iz generatekeys.sh
        String passphrase = "";	// iz generatekeys.sh

        BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("######  ChatClient  ######");
        System.out.print("[client] relative certificate path, e.g. keys/mycert.private: ");
        pubcert = user_input.readLine();
        System.out.print("[client] keystore passphrase: ");
        passphrase = user_input.readLine();

        // connect to the chat server
        try {
            KeyStore clientKS = KeyStore.getInstance("JKS");
            clientKS.load(new FileInputStream(pubcert), passphrase.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKS, passphrase.toCharArray());

            KeyStore serverKS = KeyStore.getInstance("JKS");
            serverKS.load(new FileInputStream("keys/server.public"), "public".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(serverKS);

            System.out.println("[client] connecting to chat server ...");
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            socket = (SSLSocket) sslSocketFactory.createSocket("localhost", serverPort);
            socket.setEnabledCipherSuites(new String[]{"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"}); // dovoljeni nacin kriptiranja (CipherSuite)            socket.setEnabledCipherSuites("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
            socket.startHandshake();
            listenStream = new DataInputStream(socket.getInputStream()); // create input stream for listening for incoming messages
            sendStream = new DataOutputStream(socket.getOutputStream()); // create output stream for sending messages
            System.out.println("[client] connected");

            ChatClientMessageReceiver message_receiver = new ChatClientMessageReceiver(listenStream); // create a separate thread for listening to messages from the chat server
            message_receiver.start(); // run the new thread
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
        String userInput;
        while ((userInput = user_input.readLine()) != null) { // read a line from the console
            if (userInput.charAt(0) == '@') {
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

    public static void main(String[] args) throws Exception {
        new ChatClient();
    }

    private void jMessage(String message, DataOutputStream out) {
        try {
            Instant instant = Instant.now();
            String time = "" + instant.getEpochSecond();
            String[] msg = {"j", time, message};
            out.writeUTF(EncoderDecoder.encode(msg)); // send the message to the chat server
            out.flush(); // ensure the message has been sent
        } catch (IOException e) {
            System.err.println("[system] could not send message");
            e.printStackTrace(System.err);
        }
    }

    private void zMessage(String message, DataOutputStream out) {
        try {
            Instant instant = Instant.now();
            String time = "" + instant.getEpochSecond();

            String receiver = message;
            receiver = receiver.substring(receiver.indexOf("@") + 1);
            receiver = receiver.substring(0, receiver.indexOf(" "));

            String[] msg = {"z", time, receiver, message};
            out.writeUTF(EncoderDecoder.encode(msg)); // send the message to the chat server
            out.flush(); // ensure the message has been sent
        } catch (IOException e) {
            System.err.println("[system] could not send message");
            e.printStackTrace(System.err);
        }
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
            }
        } catch (Exception e) {
            System.err.println("[system] could not read message");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void displayMessage(String message) {
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
                msg_string = String.format("%s <@\033[0;36m%s\033[0m> (%s) %s", df.format(date), msg[2], "\033[0;35mzasebno\033[0m", msg[3].substring(msg[2].length() + 2));
                break;
            case "s":
                msg_string = "[server] " + msg[2];
                break;
        }
        System.out.println(msg_string);

    }
}
