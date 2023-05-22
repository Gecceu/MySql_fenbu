package client;

import java.io.IOException;

import utils.params;

public class CilentApp {
    public static void main(String[] args) throws IOException{
        Myclient client = new Myclient();
        
        Thread masterThread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    client.connect(params.Master_ip,params.MatserPort_Client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        masterThread.start();
    }
}

