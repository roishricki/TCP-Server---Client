package Algorithems;

import Traversable.Node;

public class NodesDataBelmanF<T> {

        private Node<T> node;
        private Integer distance;
        private Integer weight;

        public NodesDataBelmanF(Node<T> node){
            this.node=node;
            this.distance= null; //present infinity
            this.weight=null; //present infinity
        }

        public NodesDataBelmanF(Node<T> node, int dist, int weight){
            this.node=node;
            this.distance=dist;
            this.weight=weight;
        }

        public Node<T> getNode() {
            return node;
        }

        public void setNode(Node<T> node) {
            this.node = node;
        }

        public int getDistance() {
            return distance;
        }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setDistance(int distance) {
            this.distance = distance;
        }


}
