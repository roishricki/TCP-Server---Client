package Algorithems;

import Matrix.Index;
import Matrix.WeightedMatrix;
import Server.Strategy.ServerReachableStrategy;
import Traversable.Node;
import Traversable.TraversableWeightedMatrix;
import Traversable.TraversableWithWeight;

import java.io.FileNotFoundException;
import java.util.*;

public class BelmanFord<T> {

    Set<NodesDataBelmanF<T>> result;
    Queue<NodesDataBelmanF<T>> discovered;

    public BelmanFord(){
        result = new HashSet<>();
        discovered = new LinkedList<>();
    }

    public NodesDataBelmanF<T> getConcreteNodesDataDijkstra (Node<T> someNode, Collection<NodesDataBelmanF<T>> list){
        for(NodesDataBelmanF<T> data : list){
            if(someNode.getData().equals(data.getNode().getData())){
                return data;
            }
        }
        return null;
    }

    public Set<NodesDataBelmanF<T>> traverse (TraversableWithWeight partOfGraph){
     Node<T> startIndex = partOfGraph.getOrigin();
     discovered.add(new NodesDataBelmanF<>(startIndex,0,partOfGraph.getConcreteWeight(startIndex)));
     while (!discovered.isEmpty()){
         NodesDataBelmanF<T> poppedNode = discovered.poll();
         Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes(poppedNode.getNode());
         for(Node<T> node : reachableNodes){
             boolean discoveredContain = false;
             boolean resultContain = false;
             for(NodesDataBelmanF<T> resultNode : result){
                 if(resultNode.getNode().equals(node)){
                     resultContain = true;
                 }
             }
             if(resultContain==false){
                 for(NodesDataBelmanF<T> discoveredNode : discovered){
                     if(discoveredNode.getNode().equals(node)){
                         discoveredContain=true;
                     }
                 }
                 if(discoveredContain==false) {
                     discovered.add(new NodesDataBelmanF<>(node, poppedNode.getDistance() + 1,
                             poppedNode.getWeight() + partOfGraph.getConcreteWeight(node)));
                     node.setParent(poppedNode.getNode());
                 }
                 else{
                     NodesDataBelmanF<T> nodeData =getConcreteNodesDataDijkstra(node,discovered);
                     if(nodeData.getWeight()>poppedNode.getWeight()+ partOfGraph.getConcreteWeight(node)){
                         nodeData.setWeight(poppedNode.getWeight()+ partOfGraph.getConcreteWeight(node));
                         nodeData.setDistance(poppedNode.getDistance()+1);
                         node.setParent(poppedNode.getNode());
                     }
                 }

             }else{
                 NodesDataBelmanF<T> nodeData =getConcreteNodesDataDijkstra(node,result);
                 if(nodeData.getWeight()>poppedNode.getWeight()+ partOfGraph.getConcreteWeight(node)){
                    nodeData.setWeight(poppedNode.getWeight()+ partOfGraph.getConcreteWeight(node));
                     nodeData.setDistance(poppedNode.getDistance()+1);
                     node.setParent(poppedNode.getNode());
                     discovered.add(nodeData);
                 }
             }
         }
         result.add(poppedNode);
     }

     return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
        ServerReachableStrategy strategy = new ServerReachableStrategy();
       WeightedMatrix matrix = new WeightedMatrix(strategy.readMatrixFromFile());
//        WeightedMatrix matrix = new WeightedMatrix(100,100);
        System.out.println(matrix);
        TraversableWeightedMatrix traversableWeightedMatrix = new TraversableWeightedMatrix(matrix);
        traversableWeightedMatrix.setStartIndex(new Index(1, 0));
        BelmanFord b = new BelmanFord();
        Set<NodesDataBelmanF<Index>> res = new HashSet<>();

        res = b.traverse(traversableWeightedMatrix);
        for (NodesDataBelmanF<Index> node : res) {
            System.out.println("[ " + node.getNode() + " : "+node.getWeight()+"      " + node.getDistance() + " ]");

        }
    }
}
