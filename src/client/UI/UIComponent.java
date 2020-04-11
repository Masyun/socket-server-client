package client.UI;

public interface UIComponent {
    void print();
    void visit(UIVisitor visitor);
}
