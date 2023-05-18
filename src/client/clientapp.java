package client;

public class clientapp {
    public static final int Client_Port = 5144;
    public static void main(String[] args){
        Myclient client = new Myclient();
        client.connect(Client_Port);
    }
}
