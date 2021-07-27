package Server.Strategy;

import Algorithems.DFSvisit;
import Algorithems.Submarine;
import Algorithems.ThreadLocalDfsVisit;
import Matrix.*;
import Traversable.Node;
import Traversable.TraversableCrossMatrix;
import Traversable.TraversableMatrix;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServerReachableStrategy implements IServerStrategy {
    @Override
    public void applyStrategy(InputStream inFromClient, OutputStream outToClient) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(inFromClient));
        BufferedWriter toClient = new BufferedWriter(new PrintWriter(outToClient));
        String clientCommand = fromClient.readLine();

        while (fromClient != null && !(clientCommand.equals("exit"))) {
            clientCommand= fromClient.readLine();
            Matrix matrix;
            while (!(clientCommand.equals("1") || clientCommand.equals("2"))) {
                toClient.write("ERROR!! Please Choose (1 or 2)\n");
                toClient.flush();
                clientCommand = fromClient.readLine();
            }
            if (clientCommand.equals("2")) {
                clientCommand = fromClient.readLine();
                int rows = Integer.parseInt(clientCommand);
                while (rows < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    rows = Integer.parseInt(clientCommand);
                }
                clientCommand = fromClient.readLine();
                int cols = Integer.parseInt(clientCommand);
                while (cols < 0) {
                    toClient.write("Dimensions Are Wrong!!\n");
                    toClient.flush();
                    toClient.write("Please try Again \n");
                    toClient.flush();
                    clientCommand = fromClient.readLine();
                    cols = Integer.parseInt(clientCommand);
                }
                matrix = new Matrix(rows, cols);
            } else {
                matrix =new Matrix(readMatrixFromFile());
            }
            toClient.write("-----------------------------------------------------------------------\n");
            toClient.write("Matrix :\n");
            toClient.write(String.valueOf(matrix));
            toClient.write("-----------------------------------------------------------------------\n");
            long startTime = System.nanoTime();
//            List<HashSet<Index>> SCC = new ArrayList<HashSet<Index>>(getAllSCCParallel(matrix)); //PLAN B
            List<HashSet<Index>> SCC = new ArrayList<HashSet<Index>>(splitMatrix(matrix));
            Comparator<HashSet<Index>> comparator = (h1, h2) -> Integer.compare(h1.size(), h2.size());
            SCC = SCC.stream().sorted(comparator).collect(Collectors.toList());

            long endTime = System.nanoTime();
            toClient.write("Connected Components :\n");
            for (HashSet<Index> uScc : SCC) {
                toClient.write(String.valueOf(uScc) + "\n");
            }
            toClient.write("stopPrint" + "\n");
            toClient.flush();
        }
    }

    /**
     * that function find all parts of scc of this matrix.
     * initialize Hashset of indices with value 1 from this matrix
     * 1. it takes random index from relevant indices (indices with value 1)
     *   and find all indices of his scc with DFSvisit.traverse function.
     * 2. add that indices to List of Hashsets, that present single scc
     * 3. removes that scc indices from relevant
     *   repeat 1-3 until relevant indices are empty
     * @param matrix
     * @return all parts of scc of this matrix
     */

    public List<HashSet<Index>> getAllSCC (Matrix matrix){
        HashSet<Index> relevantIndices = matrix.getRelevantIndices();
        List<HashSet<Index>> SCC = new ArrayList<HashSet<Index>>();
        TraversableCrossMatrix myTraversableMatrix = new TraversableCrossMatrix(matrix);
        while (!relevantIndices.isEmpty()) {
            ThreadLocalDfsVisit<Index> dfsVisit = new ThreadLocalDfsVisit<Index>();
            myTraversableMatrix.setStartIndex(relevantIndices.iterator().next());
            HashSet<Index> tempScc = new HashSet<Index>((HashSet) dfsVisit.traverse(myTraversableMatrix));
            SCC.add((HashSet<Index>) tempScc.clone());
            relevantIndices = deleteLastSCC(relevantIndices, tempScc);
        }
        return SCC;
    }

    /**
     *that function split the origin matrix to sub-matrices
     * 1. initialized executor service to number of sub-matrices
     * 2. send each sub matrix to check sub matrix function and submit it to executor service
     * 3. get all parts of SCC of sub-matrices and send it to connectSCC function
     * @param matrix - the origin matrix
     * @return the final result
     * @throws ExecutionException
     * @throws InterruptedException
     */

    public List<HashSet<Index>> splitMatrix (Matrix matrix) throws ExecutionException, InterruptedException {
        int split = 100;
        int bottomRow = 0;
        int topRow = split - 1;
        int bottomCol = 0;
        int topCol = split - 1;
        boolean iteration = false;
        int iterationsRow = getNumOfIterations(matrix.getPrimitiveMatrix().length, split);
        int iterationsCol = getNumOfIterations(matrix.getPrimitiveMatrix()[0].length, split);
        ExecutorService executorService = Executors.newFixedThreadPool(iterationsRow * iterationsCol);
        List<Future<HashSet<HashSet<Index>>>> results = new ArrayList<>();
        for (int i = 0; i < iterationsRow; i++) {
            for (int j = 0; j < iterationsCol; j++) {
                if (iteration == true) {
                    bottomCol = 0;
                    topCol = split - 1;
                    iteration = false;
                }
                int finalBottomRow = bottomRow;
                int finalTopRow = topRow;
                int finalBottomCol = bottomCol;
                int finalTopCol = topCol;
                Callable<HashSet<HashSet<Index>>> resultSubMatrix =
                        () -> initializeSubMatrix(matrix, finalBottomRow, finalTopRow, finalBottomCol, finalTopCol);
                results.add(executorService.submit(resultSubMatrix));
                if (bottomCol == 0) {
                    bottomCol = split - 1;
                } else {
                    bottomCol += split - 1;
                }
                topCol += split - 1;
            }
            iteration = true;
            if (bottomRow == 0) {
                bottomRow = split - 1;
            } else {
                bottomRow += split - 1;
            }
            topRow += split - 1;
        }
        HashSet<HashSet<HashSet<Index>>> allSCCs = new HashSet<>();
        System.out.println("Print all SCCs");
        for (Future<HashSet<HashSet<Index>>> result : results) {
            allSCCs.add(result.get());
        }
        List<HashSet<Index>> finalResult = connectSCC(allSCCs,matrix);
        return finalResult;

    }

    /**
     * check every index and collect all indices that in same scc
     * @param submatricesSccs - hash set of sccs of submatrices
     * @param matrix - origin matrix
     * @return
     */
    public List<HashSet<Index>>  connectSCC (HashSet<HashSet<HashSet<Index>>> submatricesSccs,Matrix matrix){
        System.out.println("start connectSCC function");
        HashSet<HashSet<Index>> res =new HashSet<>();
        HashSet<Index> relevantIndices = matrix.getRelevantIndices();
        for(Index index :relevantIndices){
            HashSet<Index> temp = new HashSet<>();
            temp.add(index);
            for(HashSet<HashSet<Index>> submarixScc :submatricesSccs ){
                for(HashSet<Index> connectedComponent:submarixScc){
                    if(connectedComponent.contains(index)){
                        temp.addAll(connectedComponent);
                    }
                }
            }
            res.add(temp);
        }
        List<HashSet<Index>> result = new ArrayList<>(res);
        return result;

    }

    /**
     * Returns the number of iterations for each matrix to divide it into sub-matrices
     * @param size - the size of origin matrix
     * @param split - the size of the sub matrix
     * @return
     */

    public int getNumOfIterations (int size, int split){
        int counter = 1;
        int sum = split ;
        while(sum<size){
            sum+=split-1;
            counter++;
        }
        return counter;
    }

    /**
     * - initialize submarix with concrete values from origin matrix
     * - send it to getAllSCC function and convert it to original values indices with convertToOriginalIndices function.
     * @param matrix - original matrix
     * @param bottomRow - bottom limit of row in original matrix
     * @param topRow - top limit of row in original matrix
     * @param bottomCol -bottom limit of col in original matrix
     * @param topCol- top limit of col in original matrix
     * @return all scc of this part of submatrix with original indices values
     */

    public HashSet<HashSet<Index>> initializeSubMatrix (Matrix matrix,int bottomRow,int topRow,int bottomCol,int topCol){
        int split = (topRow-bottomRow)+1;
        HashSet<Index> partofGraph = new HashSet<Index>();
        HashSet<Index> tempSCC = new HashSet<Index>();
        HashSet<HashSet<Index>> SCCoriginalIndices = new HashSet<>();
        int [][] subMatrixArray = new int[split][split];
        for(int i=0;i<split;i++){
            for(int j=0;j<split;j++){
                int originalRow = bottomRow + i;
                int originalCol = bottomCol + j;
                if(originalRow<matrix.getPrimitiveMatrix().length){
                    if(originalCol<matrix.getPrimitiveMatrix()[0].length){
                        try {
                            if (matrix.getPrimitiveMatrix()[originalRow][originalCol] == 1) {
                                subMatrixArray[i][j] = 1;
                                partofGraph.add(new Index(i, j));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        Matrix subMatrix = new Matrix(subMatrixArray);
        HashSet<HashSet<Index>> SCCwithTempIndices = new HashSet<>(getAllSCC(subMatrix));
        for(HashSet<Index> scc : SCCwithTempIndices){
            SCCoriginalIndices.add(convertToOriginalIndices(scc,bottomRow,bottomCol));
        }

        return SCCoriginalIndices;
    }

    /**
     * Convert a sub-matrix index to its index in the original matrix
     * @param scc
     * @param addToRow Add a value to a row to adjust its index
     * @param addToCol Add a value to a column to adjust its index
     * @return Hashset - fixedSCC with the relevant indices
     */

    public HashSet<Index> convertToOriginalIndices (HashSet<Index> scc,int addToRow,int addToCol){
        HashSet<Index> fixedSCC = new HashSet<>();
        for(Index index : scc){
            fixedSCC.add(new Index(index.getRow()+addToRow,index.getColumn()+addToCol));
        }
        return fixedSCC;
    }

    public HashSet<HashSet<Index>> getAllSCCParallel (Matrix matrix){
        HashSet<Index> relevantIndices = matrix.getRelevantIndices();
        TraversableCrossMatrix myTraversableMatrix = new TraversableCrossMatrix(matrix);
        ExecutorService executorService = Executors.newFixedThreadPool(relevantIndices.size());
        List<Future<HashSet<Index>>> results = new ArrayList<>();
        HashSet<HashSet<Index>> SCC = new HashSet<>();

        for(Index index : relevantIndices){
            if(myTraversableMatrix.getReachableNodes(new Node(index)).isEmpty()){
                HashSet<Index> singleNode = new HashSet<>();
                singleNode.add(index);
                SCC.add(singleNode);
            }
            else {
                myTraversableMatrix.setStartIndex(index);
                ThreadLocalDfsVisit<Index> threadLocalDfsVisit = new ThreadLocalDfsVisit();
                Callable<HashSet<Index>> callable = () -> (HashSet<Index>) threadLocalDfsVisit.traverse(myTraversableMatrix);
                results.add(executorService.submit(callable));
            }
        }
        for(Future<HashSet<Index>> scc : results){
            try {
                SCC.add(scc.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return SCC;
    }



    /**
     * @param origin - indices that there value equal to 1
     * @param scc - concrete strongly connected component
     * @return origin without the indices of scc
     */

    public HashSet<Index> deleteLastSCC (HashSet < Index > origin, HashSet < Index > scc){
        for (Index index : scc) {
            origin.remove(index);
        }
        return origin;
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




