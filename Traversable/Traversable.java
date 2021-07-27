package Traversable;

import java.util.Collection;

public interface Traversable<T> {
    public Node<T> getOrigin();
    public Collection<Node<T>> getReachableNodes(Node<T> someNode);
}