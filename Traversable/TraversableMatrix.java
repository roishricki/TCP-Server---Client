package Traversable;

import Matrix.Index;
import Matrix.Matrix;
import Traversable.Traversable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TraversableMatrix implements Traversable<Index> {
    protected final Matrix matrix;
    protected Index startIndex;

    public TraversableMatrix(Matrix matrix) {
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
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndex = new ArrayList<>();
        for (Index index : this.getNeighbors(someNode.getData())) {
            if (matrix.getValue(index) == 1) {
                Node<Index> indexNode = new Node<Index>(index, someNode);
                reachableIndex.add(indexNode);
            }
        }
        return reachableIndex;
    }
    public Collection<Index> getNeighbors (@NotNull final Index index){
        Collection<Index> list = new ArrayList<>();
        list= matrix.getNeighbors(index);
        return list;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }
}
