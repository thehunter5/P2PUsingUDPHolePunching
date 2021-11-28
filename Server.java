import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class Server {
    //initialize socket and input stream
    private DatagramSocket socket = null;

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");
    }

    public void establishConnectionAndExchangeData() throws IOException {
        byte[] received_data = new byte[Constants.DATA_BUFFER_LENGTH];
        DatagramPacket data_packet = new DatagramPacket(received_data, received_data.length);
        while (true) {
            Stack<ClientInfo> clients = new Stack<>();
            while (true) {
                socket.receive(data_packet);
                String received_message = new String(received_data, StandardCharsets.UTF_8);
                System.out.println("Connection established with client " + clients.size() + 1);
                String source_ip_address = data_packet.getAddress().toString();
                String source_port = String.valueOf(data_packet.getPort());
                ClientInfo client_info = new ClientInfo(source_ip_address, source_port);
                clients.push(client_info);
                byte message[] = "Ready!".getBytes();
                DatagramPacket packet_to_send =
                        new DatagramPacket(message, message.length,
                                InetAddress.getByName(client_info.getIp_address()), client_info.getPort());
                socket.send(packet_to_send);
                if (clients.size() == 2) {
                    System.out.println("Connection with 2 clients established ");
                    break;
                }
            }
            exchangeData(clients);
        }
    }

    private void exchangeData(Stack<ClientInfo> clients) throws IOException {
        ClientInfo client2 = clients.pop();
        ClientInfo client1 = clients.pop();
        String client2_data = client1.getIp_address() + " " + client1.getPort() + " " + Constants.KNOWN_PORT;
        DatagramPacket packet_to_send =
                new DatagramPacket(client2_data.getBytes(), client2_data.length(),
                        InetAddress.getByName(client2.getIp_address()), client2.getPort());
        socket.send(packet_to_send);
        String client1_data = client2.getIp_address() + " " + client2.getPort() + " " + Constants.KNOWN_PORT;
        packet_to_send = new DatagramPacket(client1_data.getBytes(), client1_data.length(),
                InetAddress.getByName(client1.getIp_address()), client1.getPort());
        socket.send(packet_to_send);
    }

    public void closeSocket() {
        System.out.println("Closing connection");
        // close connection
        socket.close();
    }

    public static void main(String args[]) throws IOException {
        Server server = new Server(Constants.RENDEZVOUS_SERVER_LOCAL_PORT);
        server.establishConnectionAndExchangeData();
    }
}
