package Matrix;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WeightedMatrix {
    int[][] primitiveMatrix;

    public WeightedMatrix(int[][] oArray){
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }

    public WeightedMatrix(int row,int col) {
        Random r = new Random();
        primitiveMatrix = new int[row][col];
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[0].length; j++) {
                primitiveMatrix[i][j] = r.nextInt(1000)*10;
//                if(r.nextInt(22)%7==0)
//                {
//                    primitiveMatrix[i][j]*=-1;
//                }
            }
        }
    }
    public WeightedMatrix(){
        this(5,5);
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * this function check who are the neighbors of that index
     * @param index
     * @return Collection of indices that present the neighbors of that @param index
     */
    @NotNull
    public Collection<Index> getNeighbors(@NotNull final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row+1][index.column]; //down
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column+1];//right
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column];//up
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column-1];//left
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public int getValue(@NotNull final Index index){
        return primitiveMatrix[index.row][index.column];
    }
    public void setValue(int row,int col,int value){
        primitiveMatrix[row][col]=value;
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    /**
     * this function check all indices in the matrix and find all that indices with value of 1
     * @return HashSet of indices with value = 1
     */
    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

}
