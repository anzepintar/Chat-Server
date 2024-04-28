import java.util.Base64;

public class EncoderDecoder {
    public static String encode(String[] msg) {
        String msg_string = new String();
        boolean first = true;
        for (String s : msg) {
            String tmp;
            if (first) {
                tmp = msg_string + Base64.getEncoder().encodeToString(s.getBytes());
                first = false;
            } else {
                tmp = msg_string + "_" + Base64.getEncoder().encodeToString(s.getBytes());
            }
            msg_string = tmp;
        }
        return msg_string;
    }

    public static String[] decode(String msg_string) {
        String[] msg = msg_string.split("_");
        for (int i = 0; i < msg.length; i++) {
            msg[i] = new String(Base64.getDecoder().decode(msg[i]));
        }
        return msg;
    }
}
