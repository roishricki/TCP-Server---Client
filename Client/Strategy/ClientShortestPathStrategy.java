package Client.Strategy;

import Matrix.Matrix;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientShortestPathStrategy implements IClientStrategy{
    @Override
    public void applyStrategy(InputStream inFromServer, OutputStream outToServer) throws IOException {
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(inFromServer));
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter toServer = new BufferedWriter(new PrintWriter(outToServer));

        String userCommand;
        String serverResponse;
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("----------------------- Welcome To Mission 2 --------------------------");
        System.out.println("*-*-*-* Find All Shortest Path From Start Index To Target Index *-*-*-*");
        System.out.println("                  To Continue Please Enter ---> 'start'                ");
        System.out.println("                    To Exit Please Write ---> 'exit'                   ");
        System.out.println("-----------------------------------------------------------------------");
        userCommand=userInput.readLine();
        while (!userCommand.equalsIgnoreCase("exit")){
            System.out.println("*-*-* Matrix will be NxN *-*-*");
            System.out.println("         0 <= N <= 50         ");
            System.out.println("Press 1 to Load Matrix From File");//
            System.out.println("Press 2 to Generate Matrix With Your Dimensions");//
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            while(!(userCommand.equals("1")||userCommand.equals("2"))){
                serverResponse = fromServer.readLine();
                System.out.println(serverResponse);
                userCommand = userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
            }
            if(userCommand.equals("2")) {


                System.out.println("Please Enter Dimensions Of Matrix ");
                userCommand=userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
                while (Integer.parseInt(userCommand) < 0 || Integer.parseInt(userCommand) > 50) {
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    serverResponse = fromServer.readLine();
                    System.out.println(serverResponse);
                    userCommand = userInput.readLine();
                    toServer.write(userCommand + "\n");
                    toServer.flush();
                }
            }

            System.out.println("-----------------------------------------------------------------------");
            while(!((serverResponse = fromServer.readLine()).equals("stopPrintMatrix"))){
                System.out.println(serverResponse);
            }
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("Please Choose Start Index");
            System.out.println("Enter Value of X");
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            System.out.println("Enter Value of Y");
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            while((serverResponse = fromServer.readLine()).equals("ERROR :: Please Choose Another Index"))
            {
                System.out.println("1");
                System.out.println(serverResponse);
                System.out.println("Enter Value of X");
                userCommand=userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
                System.out.println("Enter Value of Y");
                userCommand=userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
            }
            System.out.println("Please Choose Target Index");
            System.out.println("Enter Value of X");
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            System.out.println("Enter Value of Y");
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
            while((serverResponse = fromServer.readLine()).equals("ERROR :: Please Choose Another Index"))
            {
                System.out.println("Enter Value of X");
                userCommand=userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
                System.out.println("Enter Value of Y");
                userCommand=userInput.readLine();
                toServer.write(userCommand + "\n");
                toServer.flush();
            }

            while(!(serverResponse = fromServer.readLine()).equals("stopPrint")){
                System.out.println(serverResponse);
            }
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("                  To Continue Please Enter ---> 'start'                ");
            System.out.println("                    To Exit Please Write ---> 'exit'                   ");
            System.out.println("-----------------------------------------------------------------------");
            userCommand=userInput.readLine();
            toServer.write(userCommand + "\n");
            toServer.flush();
        }

        fromServer.close();
        userInput.close();
        toServer.close();
        System.out.println("*-*-* Disconnected From Server *-*-*");

    }
}
