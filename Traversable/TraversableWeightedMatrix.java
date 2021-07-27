package Traversable;

import Matrix.Index;
import Matrix.*;

import java.util.ArrayList;
import java.util.Collection;

public class TraversableWeightedMatrix implements TraversableWithWeight<Index> {
    protected final WeightedMatrix matrix;
    protected Index startIndex;

    public TraversableWeightedMatrix(WeightedMatrix matrix){
        this.matrix=matrix;
    }
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.startIndex == null) throw new NullPointerException("start index is not initialized");
        return new Node<>(this.startIndex);

    }
    public Index getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }

    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        Collection<Node<Index>> reachableNodes = new ArrayList<>();
        Collection<Index> neighborsOfNode = matrix.getNeighbors(someNode.getData());
        for(Index index : neighborsOfNode){
            reachableNodes.add(new Node(index));
        }
        return reachableNodes;
    }

    @Override
    public Integer getConcreteWeight(Node<Index> someT) {
        return Integer.valueOf(matrix.getValue(someT.getData()));

    }
}
