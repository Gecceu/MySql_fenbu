package client;

import utils.params;

public class CilentApp {
    public static void main(String[] args){
        Myclient client = new Myclient();
        client.connect(params.MatserPort_Client);
    }
}
