import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class Clientchat {
    public static void main(String[] args) throws IOException {
        System.out.println("==========================Chat room===========================");

        Socket socket = new Socket();
        Scanner sc = new Scanner(System.in);
        String line = "";
        Thread heartbeat = null;
        Thread modtager = null;

        do {
            if (line.contains("QUIT")==false) {
                line = sc.nextLine();
            }else {
                if (line.contains("JOIN")) {
                    try {
                        int comma = line.lastIndexOf(",");
                        int Colon = line.lastIndexOf(":");
                        String username = line.substring(5, comma);

                        String server_ip = line.substring(comma+1, Colon);
                        int server_port = Integer.parseInt(line.substring(Colon, line.length()));

                        InetAddress ip = InetAddress.getByName(server_ip);

                        System.out.println("Connected...");
                        System.out.println("SERVER IP: " + server_ip);
                        System.out.println("SERVER PORT: " + server_port);

                        socket = new Socket(ip, server_port);

                        OutputStream msgToServer = socket.getOutputStream();
                        InputStream msgFromServer = socket.getInputStream();

                        sendClientMessage(msgToServer, line + "\n");
                        System.out.println(msgToServer);
                        String connection = modtagMsg(msgFromServer);

                        if (connection.equals("J_OK")) {

                            modtager = new Thread(() -> {
                                while (true) {
                                    String msgIn = modtagMsg(msgFromServer);
                                    if (msgIn.equals("IOException")) {
                                        System.err.println("Error");
                                        break;
                                    } else {
                                        System.out.println(msgIn);
                                    }
                                }
                            });
                            modtager.start();

                            heartbeat = new Thread(() -> {
                                while (true) {
                                    try {
                                        String hb = "IMAV";
                                        Thread.sleep(120000);
                                        byte[] dataToSend = hb.getBytes();
                                        msgToServer.write(dataToSend);
                                        msgToServer.flush();
                                        } catch (InterruptedException e) {
                                        System.err.println("error");
                                        break;
                                    } catch (IOException e) {
                                        break;
                                    }
                                }
                            });
                            heartbeat.start();
                        } else {
                            System.out.println("SERVER: " + connection);
                            socket.close();
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        System.err.println("Unknown JOIN Command");
                    } catch (IOException e) {
                        System.err.println("Connection failed");
                    }

                } else if (line.equals("QUIT")) {
                    try {
                        OutputStream msgToServer = socket.getOutputStream();
                        sendClientMessage(msgToServer, line);
                    } catch (SocketException e) {
                    }
                } else {
                    try {
                        OutputStream msgToServer = socket.getOutputStream();
                        sendClientMessage(msgToServer, line);
                    } catch (SocketException e) {
                        System.err.println("Not connected");
                    }
                }
            }
        }
        while (!line.equals("QUIT"));
        if (heartbeat != null) {
            heartbeat.interrupt();
        }
        if (modtager != null) {
            modtager.interrupt();
        }
        socket.close();
        System.out.println("Shutting down chat...");
    }

    public static void sendClientMessage(OutputStream outputStream, String message) {
        try {
            byte[] dataToSend = message.getBytes();
            outputStream.write(dataToSend);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Message is not sent");
        }
    }

    public static String modtagMsg(InputStream inputStream) {
        String message = "";
        try {
            byte[] dataIn = new byte[1024];
            inputStream.read(dataIn);
            String msgIn = new String(dataIn);
            message = msgIn.trim();

        } catch (IOException e) {
            message = "Error";
        }
        return message;
    }

}