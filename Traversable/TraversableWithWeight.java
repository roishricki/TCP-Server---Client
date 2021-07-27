package Traversable;
public interface TraversableWithWeight<T> extends Traversable<T> {
    public Integer getConcreteWeight (Node<T> someNode);
}
