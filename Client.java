import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private DatagramSocket socket = null;
    String ip_address = null;
    int sport = -1;
    int dport = -1;

    public Client(int port) throws SocketException {
        socket = new DatagramSocket(port);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");
    }

    public void handshake() throws IOException {
        System.out.println("Trying to establish connection");
        byte message[] = "0".getBytes();
        DatagramPacket packet_to_send =
                new DatagramPacket(message, message.length,
                        InetAddress.getByName(Constants.RENDEZVOUS_SERVER_IP),Constants.RENDEZVOUS_SERVER_LOCAL_PORT);
        socket.send(packet_to_send);
    }

    public void waitForServerResponse() throws IOException {
        while (true) {
            byte[] received_data = new byte[Constants.DATA_BUFFER_LENGTH];
            DatagramPacket packet_to_receive = new DatagramPacket(received_data, received_data.length);
            socket.receive(packet_to_receive);
            String received_message = new String(received_data, StandardCharsets.UTF_8);
            if (received_message == "Ready!") {
                System.out.println("Connection established");
                break;
            }
        }
    }

    public void waitForPeerData() throws IOException {
        byte[] received_data = new byte[Constants.DATA_BUFFER_LENGTH];
        DatagramPacket packet_to_receive = new DatagramPacket(received_data, received_data.length);
        socket.receive(packet_to_receive);
        String received_message = new String(received_data, StandardCharsets.UTF_8);
        String peer_data[] = received_message.split(" ");
        this.ip_address = peer_data[0];
        this.sport = Integer.getInteger(peer_data[1]);
        this.dport = Integer.getInteger(peer_data[2]);
        System.out.println("ip_address: " + ip_address + " sport: " + sport + " dport: " + dport);
    }

    public void punchHoleInUdp() throws IOException {
        DatagramSocket udp_hole_punch_socket = new DatagramSocket(this.sport);
        byte message[] = "0".getBytes();
        DatagramPacket packet_to_send =
                new DatagramPacket(message, message.length,
                        InetAddress.getByName(this.ip_address), this.dport);
        udp_hole_punch_socket.send(packet_to_send);
    }

    public void listenToPeer() throws IOException {
        DatagramSocket listen_socket = new DatagramSocket(this.sport);
        while (true) {
            byte[] received_data = new byte[Constants.DATA_BUFFER_LENGTH];
            DatagramPacket packet_to_receive = new DatagramPacket(received_data, received_data.length);
            listen_socket.receive(packet_to_receive);
            String received_message = new String(received_data, StandardCharsets.UTF_8);
            System.out.println("Peer: " + received_message);
        }
    }

    public void sendToPeer() throws IOException {
        DatagramSocket send_to_peer_socket = new DatagramSocket(this.dport);
        while (true) {
            byte message[] = "0".getBytes();
            DatagramPacket packet_to_send =
                    new DatagramPacket(message, message.length,
                            InetAddress.getByName(this.ip_address), this.sport);
            send_to_peer_socket.send(packet_to_send);
        }
    }

    public void run() {
        try{
            listenToPeer();
        } catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught" +e);
        }
    }

    public static void main(String args[]) throws IOException {
        Client clientSender = new Client(Constants.CLIENT_LOCAL_PORT);
        Client clientReceiver = new Client(Constants.CLIENT_LOCAL_PORT);
        clientSender.handshake();
        clientSender.waitForServerResponse();
        clientSender.waitForPeerData();
        clientSender.punchHoleInUdp();
        clientReceiver.start();
        clientSender.sendToPeer();
    }
}
