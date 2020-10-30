package uab.cs420.farm_dashboard;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.TreeItem;


public class Item extends Component {
    private TreeItem<String> treeItem;
    private ItemContainer parent = null;

    Item(String name, float price, Point2D position, Point3D size) {
        super(name, price, position, size);
        this.treeItem = new TreeItem<String>(name);
    }

    public TreeItem<String> getTreeItem() {
        return this.treeItem;
    }

    public ItemContainer getParent() {
        return this.parent;
    }

    public void setParent(ItemContainer itemC) {
        this.parent = itemC;
    }

    public Item findByTreeItem(TreeItem<String> treeItem) {
        if (this.treeItem == treeItem) {
            return this;
        }
        return null;
    }
}
