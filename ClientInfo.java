import lombok.Getter;

@Getter
public class ClientInfo {
    private String ip_address = null;
    private int port = -1;

    public ClientInfo(String ip_address, String port) {
        this.ip_address = ip_address;
        this.port = Integer.getInteger(port);
    }
}
