package uab.cs420.farm_dashboard;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class ItemContainer extends Item {
    private ArrayList<Item> items = new ArrayList<>();


    ItemContainer(String name, float price, Point2D position, Point3D size) {
        super(name, price, position, size);
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setParent(this);
        this.getTreeItem().getChildren().add(item.getTreeItem());
    }

    public void clearItems() {
        items.clear();
    }

    public void removeItem(Item item) {
        items.remove(item);
        this.getTreeItem().getChildren().remove(item.getTreeItem());
    }

    public float getPrice() {
        float price = super.getPrice();
        for (Item i : items)
            price += i.getPrice();
        return price;
    }

    public Item findByTreeItem(TreeItem<String> treeItem) {
        if (this.getTreeItem() == treeItem) {
            return this;
        }
        for (Item i : this.items) {
            Item found = i.findByTreeItem(treeItem);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void drawRepresentation(Pane canvas) {
        super.drawRepresentation(canvas);
        for (Item i : this.items) i.drawRepresentation(canvas);
    }

    public void eraseRepresentation(Pane canvas) {
        super.eraseRepresentation(canvas);
        for (Item i : this.items) i.eraseRepresentation(canvas);
    }
}
