package Client;

import Client.Strategy.*;
import Matrix.Matrix;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;

public class Client {
    private InetAddress serverIP;
    private int serverPort;
    private IClientStrategy strategy;

    public Client(InetAddress serverIP, int serverPort, IClientStrategy strategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.strategy = strategy;

    }


    public static void main(String[] args) throws IOException {

        Client client = new Client(InetAddress.getLocalHost(),8010,new ClientShortestPathStrategy());
        client.start();
    }

    public void start() throws IOException {
        Socket clientSocket = new Socket("127.0.0.1",8010);
        System.out.println("*-*-* Connected to server *-*-*");
        strategy.applyStrategy(clientSocket.getInputStream(),clientSocket.getOutputStream());

    }
}
