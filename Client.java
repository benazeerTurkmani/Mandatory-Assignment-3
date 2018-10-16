import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String ipaddress;
    private String username;
    private boolean isConnected = true;


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ipaddress;
    }

    public void setIp(String ip) {
        this.ipaddress = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InputStream getInput() {
        return inputStream;
    }

    public void setInput(InputStream input) {
        this.inputStream = input;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

}