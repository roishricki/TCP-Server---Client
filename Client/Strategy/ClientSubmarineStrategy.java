package Client.Strategy;

import java.io.*;

public class ClientSubmarineStrategy implements IClientStrategy {
    @Override
    public void applyStrategy(InputStream inFromServer, OutputStream outToServer) throws IOException {
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(inFromServer));
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter toServer = new BufferedWriter(new PrintWriter(outToServer));

        String userCommand;
        String serverResponse;
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("----------------------- Welcome To Mission 3 --------------------------");
        System.out.println("*-*-*-* Check If Is It Valid Matrix And Return Number Of Submarines *-*-*-*");
        System.out.println("                  To Continue Please Enter ---> 'start'                ");
        System.out.println("                    To Exit Please Write ---> 'exit'                   ");
        System.out.println("-----------------------------------------------------------------------");
        userCommand = userInput.readLine();
        while (!userCommand.equalsIgnoreCase("exit")) {
            System.out.println("Press 1 to Load Matrix From File");
            System.out.println("Press 2 to Generate Matrix With Your Dimensions");
            userCommand = userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            while (!(userCommand.equals("1") || userCommand.equals("2"))) {
                serverResponse = fromServer.readLine();
                System.out.println(serverResponse);
                userCommand = userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
            }
            if (userCommand.equals("2")) {

                System.out.println("Please Enter Dimensions Of Matrix ");
                System.out.println("Number Of Rows :");
                userCommand = userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
                while (Integer.parseInt(userCommand) < 0) {
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    System.out.println("Number Of Rows :");
                    userCommand = userInput.readLine();
                    toServer.write(userCommand + "\n");
                    toServer.flush();
                }
                System.out.println("Number Of Cols :");
                userCommand = userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
                while (Integer.parseInt(userCommand) < 0) {
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    System.out.println("Number Of Cols :");
                    userCommand = userInput.readLine();
                    toServer.write(userCommand + "\n");
                    toServer.flush();
                }
            }
            System.out.println("-----------------------------------------------------------------------");
//            serverResponse=fromServer.readLine();
            while (!((serverResponse = fromServer.readLine()).equals("stopPrintMatrix"))) {
                System.out.println(serverResponse);
            }
            System.out.println("-----------------------------------------------------------------------");
            while (!(serverResponse = fromServer.readLine()).equals("stopPrint")) {
                System.out.println(serverResponse);
            }

            System.out.println("-----------------------------------------------------------------------");
            System.out.println("                  To Continue Please Enter ---> 'start'                ");
            System.out.println("                    To Exit Please Write ---> 'exit'                   ");
            System.out.println("-----------------------------------------------------------------------");
            userCommand = userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();

        }
    }
}
