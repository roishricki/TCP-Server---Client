package Traversable;

import Matrix.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TraversableCrossMatrix implements Traversable <Index> {
    protected final Matrix matrix;
    protected Index startIndex;


    public TraversableCrossMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        return new Node<>(this.startIndex);

    }

    @Override
    /**
     *The function finds all achievable neighbors whose value is equal to 1
     * @param someNode -source index
     * @return list of reachable indices
     */
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.getCrossNeighbors(someNode.getData())) {
            if (matrix.getValue(index) == 1) {
                Node<Index> indexNode = new Node<Index>(index, someNode);
                reachableIndex.add(indexNode);
            }
        }
        return reachableIndex;
    }

    /**
     * The function finds all achievable neighbors
     * @param index - source index
     * @return list of all neighbors (right, left, up, down, and diagonals )
     */
    public Collection<Index> getCrossNeighbors(@NotNull final Index index){
        Collection<Index> list = new ArrayList<>();
        list= matrix.getNeighbors(index);
        int extracted = -1 ;
        try{
            extracted = matrix.getPrimitiveMatrix()[index.getRow()-1][index.getColumn()-1];//left-up
            list.add(new Index(index.getRow()-1,index.getColumn()-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = matrix.getPrimitiveMatrix()[index.getRow()-1][index.getColumn()+1];//right-up
            list.add(new Index(index.getRow()-1,index.getColumn()+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = matrix.getPrimitiveMatrix()[index.getRow()+1][index.getColumn()+1];//right-down
            list.add(new Index(index.getRow()+1,index.getColumn()+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = matrix.getPrimitiveMatrix()[index.getRow()+1][index.getColumn()-1];//left-down
            list.add(new Index(index.getRow()+1,index.getColumn()-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}

        return list;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}
