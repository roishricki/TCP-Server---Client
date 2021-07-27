package Server.Strategy;

import Algorithems.Submarine;
import Matrix.*;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.*;

public class ServerSubmarineStrategy implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(inFromClient));
        BufferedWriter toClient = new BufferedWriter(new PrintWriter(outToClient));
        boolean iteration = false;

        String clientCommand = fromClient.readLine();
        if(clientCommand == null){clientCommand="exit";}
        while (fromClient != null && !(clientCommand.equals("exit"))) {
            if(iteration==true){
                clientCommand= fromClient.readLine();
            }
            Matrix matrix;
            while(!(clientCommand.equals("1")||clientCommand.equals("2"))){
                /*
                  1 to generate matrix with your dimensions
                  2 to load matrix from file
                 */
                toClient.write("ERROR!! Please Choose (1 or 2)\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
            }
            if(clientCommand.equals("2")) {
                clientCommand = fromClient.readLine();
                int rows=Integer.parseInt(clientCommand);
                while (rows < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    rows = Integer.parseInt(clientCommand);
                }
                clientCommand = fromClient.readLine();
                int cols=Integer.parseInt(clientCommand);
                while (cols < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    cols = Integer.parseInt(clientCommand);
                }

                matrix = new Matrix(rows, cols);
            }
            else  {
                matrix = readMatrixFromFile();
            }
            toClient.flush();
            toClient.write(String.valueOf(matrix)+"\n");
            toClient.write("stopPrintMatrix\n");
            toClient.flush();
            Submarine submarineAlgo = new Submarine();
            int numOfSubmarines=submarineAlgo.checkSubmarines(matrix);
            if(numOfSubmarines==-1){
                toClient.write("*-*-*-* Matrix Is Invalid ! *-*-*-*\n");
                toClient.flush();
            }else {
                toClient.write("*-*-*-* There is "+numOfSubmarines+" Submarines *-*-*-*\n");
                toClient.flush();
            }
            toClient.write("stopPrint\n");
            toClient.flush();
            clientCommand=fromClient.readLine();
            iteration=true;
        }
    }




    /**
     *
     * @return matrix from file
     * @throws FileNotFoundException
     */

    public Matrix readMatrixFromFile() throws FileNotFoundException {
        int maxRows = 0;
        int maxColumn = 0;
        try {
            File inputFile = new File("src/matrix.txt");
            Scanner scanF = new Scanner(inputFile);
            String line = scanF.nextLine();
            while (line != null) {
                if (line.trim().length() > 0) {
                    maxRows++;
                    String valVar[] = line.split(" ");
                    if (maxColumn < valVar.length) {
                        maxColumn = valVar.length;
                    }
                }
                try {
                    line = scanF.nextLine();
                } catch (NoSuchElementException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(new BufferedReader(new FileReader("src/matrix.txt")));
        int[][] myMatrix = new int[maxRows][maxColumn];
        while (sc.hasNextLine()) {
            for (int i = 0; i < myMatrix.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    myMatrix[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
        Matrix matrix = new Matrix(myMatrix);
        return matrix;
    }


}
