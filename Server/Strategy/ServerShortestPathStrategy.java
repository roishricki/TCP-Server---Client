package Server.Strategy;

import Algorithems.BFSvisit;
import Algorithems.NodesDataBFS;
import Matrix.*;
import Traversable.Node;
import Traversable.TraversableCrossMatrix;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerShortestPathStrategy implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) throws IOException, ClassNotFoundException {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(inFromClient));
        BufferedWriter toClient = new BufferedWriter(new PrintWriter(outToClient));

        String clientCommand = fromClient.readLine();
        if(clientCommand == null){clientCommand="exit";}
        while (fromClient != null && !(clientCommand.equals("exit"))) {
            Matrix matrix;
            while(!(clientCommand.equals("1")||clientCommand.equals("2"))){
                toClient.write("ERROR!! Please Choose (1 or 2)\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
            }
            if(clientCommand.equals("2")) {
                clientCommand = fromClient.readLine();
                int dimensions = Integer.parseInt(clientCommand);
                while ( dimensions > 50 || dimensions < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    dimensions = Integer.parseInt(clientCommand);
                }
                matrix = new Matrix(dimensions, dimensions);
            }
            else  {
                matrix = new Matrix(readMatrixFromFile());
            }
            toClient.write(String.valueOf(matrix)+"\n");
            toClient.write("stopPrintMatrix\n");
            toClient.flush();
            clientCommand = fromClient.readLine();
            int row = Integer.parseInt(clientCommand);
            clientCommand = fromClient.readLine();
            int col = Integer.parseInt(clientCommand);
            Index startIndex = new Index(row, col);
            while (matrix.getValue(startIndex)!=1){
                toClient.flush();
                toClient.write("ERROR :: Please Choose Another Index\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
                 row = Integer.parseInt(clientCommand);
                clientCommand = fromClient.readLine();
                 col = Integer.parseInt(clientCommand);
                 startIndex = new Index(row, col);
                 toClient.write("succes\n");
                 toClient.flush();
            }
            toClient.write("succes\n");
            toClient.flush();
            clientCommand = fromClient.readLine();
            row = Integer.parseInt(clientCommand);
            clientCommand = fromClient.readLine();
            col = Integer.parseInt(clientCommand);
            Index targetIndex = new Index(row,col);
            while(matrix.getValue(targetIndex)!=1){
                toClient.flush();
                toClient.write("ERROR :: Please Choose Another Index\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
                row = Integer.parseInt(clientCommand);
                clientCommand = fromClient.readLine();
                col = Integer.parseInt(clientCommand);
                startIndex = new Index(row, col);
                toClient.write("succes\n");
                toClient.flush();
            }
            toClient.write("succes\n");
            toClient.flush();
            Index finalStartIndex = startIndex;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Callable <List<Queue<Index>>> allPathCallable =()-> findAllPaths(matrix, finalStartIndex,targetIndex);
            Future<List<Queue<Index>>> allPathFuture = executorService.submit(allPathCallable);

            try {
                if(allPathFuture.get()==null){
                    toClient.write("-----------------------------------------------------------------------\n");
                    toClient.write("Target Index Is Not Reachable \n");
                }
                else{
                for(Queue<Index> queue : allPathFuture.get()) {
                    toClient.write(String.valueOf(queue) + "\n");
                  }
                }
                toClient.write("stopPrint" + "\n");
                toClient.flush();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clientCommand=fromClient.readLine();

        }
    }

    public List<Queue<Index>> findAllPaths(Matrix matrix, Index startIndex, Index targetIndex){

        TraversableCrossMatrix traversableMatrix= new TraversableCrossMatrix(matrix);
        traversableMatrix.setStartIndex(startIndex);
        BFSvisit bfSvisit = new BFSvisit();
        Set<NodesDataBFS<Index>> nodesDataBFS = bfSvisit.traverse(traversableMatrix);
        if(!nodesDataBFS.contains(bfSvisit.getConcreteDataBFS(targetIndex))){
            return null;
        }
        Queue<NodesDataBFS<Index>> checkList = new LinkedList<>();
        HashMap<Index, Collection<Index>> paths = new HashMap<Index, Collection<Index>>();
        checkList.add(bfSvisit.getConcreteDataBFS(targetIndex));
        while (!checkList.isEmpty()){
            NodesDataBFS<Index> temp = checkList.poll();
            Collection<Node<Index>> reachableNodes = traversableMatrix.getReachableNodes(temp.getNode());
            Collection<Index> concreteReachableNodes = new HashSet<>();
            for(Node<Index> node : reachableNodes){
                if(temp.getDistance() - 1  == bfSvisit.getConcreteDataBFS(node.getData()).getDistance()){
                    checkList.add(bfSvisit.getConcreteDataBFS(node.getData()));
                    concreteReachableNodes.add(node.getData());
                }
            }
            paths.put(temp.getNode().getData(), concreteReachableNodes);
        }
        Queue<Queue<Index>> checkPaths = new LinkedList<>();
        Queue<Index> t = new LinkedList<Index>();
        List<Queue<Index>> result = new ArrayList<>();
        t.add(targetIndex);
        checkPaths.add(t);
        while(!checkPaths.isEmpty()){
            Queue<Index> tempList = new LinkedList<Index>();
            tempList.addAll(checkPaths.poll());
            if(tempList.peek().equals(startIndex)){
                result.add(new LinkedList<Index>(tempList));
            }
            else {
                Collection<Index> collection = paths.get(tempList.peek());
                for (Index index : collection) {
                    Collections.reverse((List<?>) tempList);
                    tempList.add(index);
                    Collections.reverse((List<?>) tempList);
                    checkPaths.add(new LinkedList<Index>(tempList));
                    tempList.poll();
                }
            }
        }
        return result;
    }

    /**
     * this function read matrix template like this :
     * 1 0 1 0 1 0
     * 0 0 0 0 0 0
     * 1 0 1 1 1 0
     * 1 0 0 1 0 0
     * A matrix between each element has only a space
     * Without square brackets
     * @return Matrix
     * @throws FileNotFoundException
     */
    public int[][] readMatrixFromFile() throws FileNotFoundException {
        /*
         before we want to fill the matrix we need to initialize the length of the row and column
         */
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
        /*
        fill the matrix in the value we have in the TXT file
         */
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
        return myMatrix;
    }

}
