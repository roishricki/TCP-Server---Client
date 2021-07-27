package Server.Strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public interface IServerStrategy {
    void applyStrategy(InputStream inFromClient, OutputStream outToClient) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException;

}
