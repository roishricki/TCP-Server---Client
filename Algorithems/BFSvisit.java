package Algorithems;

import Traversable.Node;
import Traversable.*;

import java.util.*;

public class BFSvisit <T>{

    Set<Node<T>> finished;     // set for finished nodes
    Set<NodesDataBFS<T>> result;  // set of NodesData for results
    Queue<NodesDataBFS<T>> discoveredQueue;  // queue of NodesData for discovered nodes

    public BFSvisit(){
        finished=new HashSet<Node<T>>();
        result=new HashSet<NodesDataBFS<T>>();
        discoveredQueue= new LinkedList<NodesDataBFS<T>>();

    }

    /**
     * @param node
     * @return NodesData of node
     */
    public NodesDataBFS<T> getConcreteDataBFS(T node){
        for(NodesDataBFS<T> nodesData : result){
            if(nodesData.getNode().getData().equals(node))
                return nodesData;
        }
        return null;
    }

    /**
     * Push to queue the starting node of our graph V
     * While queue is not empty: // there are nodes to handle V
     *     removed = poll operation V
     *     add to finish set V
     *      invoke getReachableNodes on the removed node V
     *     For each reachable node:
     *         if the current reachable node is not in finished set && checkList queue not include that node
     *         - set the popped node to parent of that node.
     *         - add that node to discovered queue with distance of his parent +1
     * @param partOfGraph
     * @return set of NodesData that include distances from start index to other indices
     */
    public Set<NodesDataBFS<T>> traverse(Traversable partOfGraph){
        Queue<Node<T>> checkList = new LinkedList<Node<T>>();
        Node<T> startIndex = partOfGraph.getOrigin();
        discoveredQueue.add(new NodesDataBFS<>(startIndex,0));
        checkList.add(startIndex);
        while (!checkList.isEmpty()){
            NodesDataBFS<T> poppedNode = discoveredQueue.poll();
            finished.add(checkList.poll());
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode.getNode());
            for(Node<T> node : reachableNodes){
                if(!finished.contains(node) && !checkList.contains(node)){
                    node.setParent(poppedNode.getNode());
                    discoveredQueue.add(new NodesDataBFS<>(node,poppedNode.getDistance() + 1 ));
                    checkList.add(node);
                }
            }
            result.add(poppedNode);
        }
        return result;
    }

}