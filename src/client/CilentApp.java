package client;

import java.io.IOException;

import utils.params;

public class CilentApp {
    private static final String MASTER_IP = "127.0.0.1";
    
    public static void main(String[] args) throws IOException{
        Myclient client = new Myclient();
        
        Thread masterThread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    client.connect(MASTER_IP,params.MatserPort_Client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        masterThread.start();
    }
}

