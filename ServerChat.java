import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ServerChat {
    public static void main(String[] args){
        System.out.println("==========================Server===========================");

        ArrayList<Client> clients = new ArrayList<>();
        final int PORT_LISTEN = 5656;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT_LISTEN);
            System.out.println("About to accept client connection...");

            while (true) {

                //opretter forbindelsen mellem server og client
                final Socket socket = serverSocket.accept();
                System.out.println("Connection was made to Clint...");
                String clientIp = socket.getInetAddress().getHostAddress();

                String username = " ";

                try {
                    InputStream inputStream = socket.getInputStream();


                    byte[] dataIn = new byte[1024];
                    inputStream.read(dataIn);
                    String msgIn = new String(dataIn);
                    msgIn = msgIn.trim();
                    System.out.println(msgIn);

                    if (msgIn.contains("JOIN")) {
                        int Comma = msgIn.lastIndexOf(",");
                        username = msgIn.substring(5, Comma);

                        System.out.println("IP: " + clientIp + "\n" + "PORT: " + socket.getPort() + "\n" + "Username: " + username+"\n");

                        String newClient = "New Client: " + username + " has now joined the chat \n";
                        MessageToAllClients(clients, newClient);

                        ValidateInput validate = new ValidateInput().getInstance(clients);
                        validate.checkUsername(username);

                        if (validate.isCheck()) {

                            sendMSG(socket.getOutputStream(), validate.getMessage());


                            Client client = new Client();
                            client.setSocket(socket);
                            client.setInput(socket.getInputStream());
                            client.setOutputStream(socket.getOutputStream());
                            client.setIp(socket.getInetAddress().getHostAddress());
                            client.setUsername(username);
                            client.setConnected(true);
                            clients.add(client);

                            String allClients = "Clients: ";
                            for (Client cln : clients) {
                                allClients += cln.getUsername() + ", ";
                            }

                            allClients = allClients.substring(0, allClients.length()-2);

                            allClients += "\n";

                            MessageToAllClients(clients, allClients);

                            ArrayList<Thread> modtager = new ArrayList<>();
                            Thread modtagerThread = new Thread(() -> {
                                while (client.isConnected()) {
                                    try {

                                        InputStream input = client.getInput();
                                        byte[] data = new byte[1024];
                                        input.read(data);
                                        String msgFromClient = new String(data);
                                        msgFromClient = msgFromClient.trim();


                                        if (msgFromClient.contains("JOIN")) {

                                        } else if (msgFromClient.equalsIgnoreCase("quit")){
                                            System.out.println("This Client: " + client.getUsername() + " left");
                                            client.setConnected(false);
                                            break;

                                        } else if (msgFromClient.equals("IMAV")) {
                                            //
                                        } else if(msgFromClient.contains("DATA")){
                                            int message = msgFromClient.indexOf(":");
                                            msgFromClient = msgFromClient.substring(message+2);
                                            String ToAllClients = " : " + msgFromClient+"\n";
                                            MessageToAllClients(clients, ToAllClients);
                                            System.out.println(ToAllClients);
                                        } else {
                                            //String msgToAllClients = " : " + msgFromClient+"\n";
                                            //System.out.println(msgToAllClients);
                                            //MessageToAllClients(clients, msgToAllClients);
                                        }
                                    } catch (IOException e) {
                                        clients.remove(client);
                                        MessageToAllClients(clients,"This user: " + client.getUsername() + " has left the chat \n");
                                        client.setConnected(false);
                                        break;
                                    }

                                }
                            });
                            modtager.add(modtagerThread);

                            for (Thread t : modtager) {
                                t.start();
                            }
                        } else{
                            sendMSG(socket.getOutputStream(), validate.getMessage());
                            socket.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void MessageToAllClients(ArrayList<Client> clients, String message){
        for (Client client : clients) {
            if (client.isConnected()) {
                sendMSG(client.getOutputStream(), message);
            }
        }
    }

    public static void sendMSG(OutputStream output, String message) {
        try {
            byte[] dataToSend = message.getBytes();
            output.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ValidateInput {

    private static ValidateInput instance = new ValidateInput();
    private String message;
    private boolean check;
    private static ArrayList<Client> clients;

    public String getMessage() {
        return message;
    }

    public boolean isCheck() {
        return check;
    }

    public void checkUsername(String username){
        checkUsername(username, clients);
    }

    public void checkUsername(String username, ArrayList<Client> clients) {
        if (username.matches("^[a-zA-Z-0-9_]{0,12}$")) {
            check = true;
            message = "J_OK: ok JOIN \n";
        } else{
            check = false;
            message = "J_ER: Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters. \n";
            System.out.println(message);
        }
        for (Client c : clients) {
            if (c.getUsername().equals(username)) {
                check = false;
                message = "J_ER 111: Username is taken \n";
                System.out.println(message);
            }
        }
    }

    public static ValidateInput getInstance(ArrayList<Client> clients) {
        ValidateInput.clients = clients;
        return instance;
        }


}

