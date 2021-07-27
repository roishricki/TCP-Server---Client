package Server.Strategy;

import Algorithems.BelmanFord;
import Algorithems.NodesDataBelmanF;
import Matrix.*;
import Traversable.Node;
import Traversable.TraversableWeightedMatrix;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerMinWeightStrategy implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) throws IOException, ClassNotFoundException {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(inFromClient));
        BufferedWriter toClient = new BufferedWriter(new PrintWriter(outToClient));
        boolean iteration = false;

        String clientCommand = fromClient.readLine();
        if (clientCommand == null) {
            clientCommand = "exit";
        }
        while (fromClient != null && !(clientCommand.equals("exit"))) {
            if (iteration == true) {
                clientCommand = fromClient.readLine();
            }
            WeightedMatrix matrix;
            while (!(clientCommand.equals("1") || clientCommand.equals("2"))) {
                toClient.write("ERROR!! Please Choose (1 or 2)\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
            }
            if (clientCommand.equals("2")) {
                clientCommand = fromClient.readLine();
                int dimensions = Integer.parseInt(clientCommand);
                while ( dimensions < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    dimensions = Integer.parseInt(clientCommand);
                }
                matrix = new WeightedMatrix(dimensions, dimensions);
            } else {
                matrix = new WeightedMatrix(readMatrixFromFile());
            }
            toClient.write(String.valueOf(matrix) + "\n");
            toClient.write("stopPrintMatrix\n");
            toClient.flush();
            clientCommand = fromClient.readLine();
            int row = Integer.parseInt(clientCommand);
            clientCommand = fromClient.readLine();
            int col = Integer.parseInt(clientCommand);
            Index startIndex = new Index(row, col);
            clientCommand = fromClient.readLine();
            row = Integer.parseInt(clientCommand);
            clientCommand = fromClient.readLine();
            col = Integer.parseInt(clientCommand);
            Index targetIndex = new Index(row, col);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Callable<List<Queue<Index>>> allPathCallable =()-> findAllPathParallel(startIndex, targetIndex,matrix);
            Future<List<Queue<Index>>> allPathsFuture = executorService.submit(allPathCallable);
            try {
                allPathsFuture.get();
                toClient.write("-----------------------------------------------------------------------\n");
                for(Queue<Index> singlePath : allPathsFuture.get()){
                    toClient.write(String.valueOf(singlePath) + "\n");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            toClient.write("stopPrint" + "\n");
            toClient.flush();
            clientCommand = fromClient.readLine();
            iteration = true;
        }

        toClient.close();
        fromClient.close();

    }


    public List<Queue<Index>> findAllPathParallel (Index startIndex,Index targetIndex,WeightedMatrix matrix){
        TraversableWeightedMatrix traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);
        traversableWeightedMatrix.setStartIndex(startIndex);
        BelmanFord bf = new BelmanFord();
        Set<NodesDataBelmanF<Index>> weightToNodes = bf.traverse(traversableWeightedMatrix);
        NodesDataBelmanF<Index> dataTarget = bf.getConcreteNodesDataDijkstra(new Node(targetIndex),weightToNodes);
        Collection<Index> reachableToTarget = matrix.getNeighbors(targetIndex);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Future<List<Queue<Index>>>> futureList = new ArrayList<>();
        for(Index index : reachableToTarget) {
            if (bf.getConcreteNodesDataDijkstra(new Node(index), weightToNodes).getWeight() ==
                    dataTarget.getWeight() - matrix.getValue(dataTarget.getNode().getData())){
                Callable<List<Queue<Index>>> allPathCallable =()-> findAllPaths(startIndex, index,matrix);
                futureList.add(executorService.submit(allPathCallable));
            }
        }
        List<List<Queue<Index>>> results = new ArrayList<>();
        List<Queue<Index>> allPaths = new LinkedList<>();
        for(Future<List<Queue<Index>>> singleFuture : futureList){
            try {
                results.add(singleFuture.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(List<Queue<Index>> singleRes : results){
            for(Queue<Index> singlePath : singleRes){
                singlePath.add(targetIndex);
                allPaths.add(singlePath);
            }
        }
        return allPaths;
    }


    public List<Queue<Index>> findAllPaths (Index startIndex, Index targetIndex, WeightedMatrix matrix){
        TraversableWeightedMatrix traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);
        traversableWeightedMatrix.setStartIndex(startIndex);
        BelmanFord bf = new BelmanFord();
        Set<NodesDataBelmanF<Index>> weightToNodes = bf.traverse(traversableWeightedMatrix);
        HashMap<Index, Collection<Index>> paths = new HashMap<Index, Collection<Index>>();
        Queue<NodesDataBelmanF<Index>> workingQueue = new LinkedList<>();
        workingQueue.add(bf.getConcreteNodesDataDijkstra(new Node(targetIndex),weightToNodes));
        while (!workingQueue.isEmpty()) {
            NodesDataBelmanF<Index> poppedIndex = workingQueue.poll();
            Collection<Index> reachableIndices = new LinkedList<Index>(matrix.getNeighbors((Index) poppedIndex.getNode().getData()));
            Collection<Index> concreteReachableNodes = new HashSet<>();
            for (Index index : reachableIndices) {
                if (bf.getConcreteNodesDataDijkstra(new Node(index), weightToNodes).getWeight() ==
                        poppedIndex.getWeight() - matrix.getValue(poppedIndex.getNode().getData())) {
                    NodesDataBelmanF<Index> temp = bf.getConcreteNodesDataDijkstra(new Node(index), weightToNodes);
                    workingQueue.add(temp);
                    concreteReachableNodes.add(index);
                }
            }
            paths.put(poppedIndex.getNode().getData(), concreteReachableNodes);
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
