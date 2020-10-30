package uab.cs420.farm_dashboard;


import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;

public class Controller {
    private static Controller controller = null;
    private final ItemContainer farm = new ItemContainer(
            "farm",
            0,
            new Point2D(0, 0),
            new Point3D(600, 800, 0)
    );
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button addItemButton;
    @FXML
    private Button addItemConButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField nameTextBox;
    @FXML
    private TextField xCoordTextBox;
    @FXML
    private TextField yCoordTextBox;
    @FXML
    private TextField lengthTextBox;
    @FXML
    private TextField widthTextBox;
    @FXML
    private TextField heightTextBox;
    @FXML
    private TextField priceTextBox;
    @FXML
    private Button saveButton;
    @FXML
    private Pane inputPane;
    @FXML
    private Pane plotPane;
    @FXML
    private Button initDrone;
    @FXML
    private Button scanFarm;
    @FXML
    private Button visitItem;
    @FXML
    private Button returnHome;
    private int numItems = 0;

    private Drone drone;

    private ItemContainer commandCenter;

    private Main main;

    private Controller() {
    }

    public static Controller getInstance() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * when the fxml file is loaded.
     */
    @FXML
    public void initialize() {
        this.farm.setSize(new Point3D(plotPane.getWidth(), plotPane.getHeight(), 0));
        treeView.setRoot(this.farm.getTreeItem());
        inputPane.setVisible(false);
    }

    private Item getSelectedItem() {
        TreeItem<String> selectedNode = treeView.getSelectionModel().getSelectedItem();
        if (selectedNode == null) {
            selectedNode = treeView.getRoot();
        }
        return this.farm.findByTreeItem(selectedNode);
    }

    @FXML
    private void addItem() {
        Item selectedItem = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedItem instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedItem;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Creating Item");
            alert.setContentText("Cannot add an Item to an Item.");
            alert.show();
            return;
        }

        Item newItem = new Item(
                "New_Item" + this.numItems,
                0,
                new Point2D(0, 0),
                new Point3D(50, 50, 0)
        );
        this.numItems++;
        selectedItemContainer.addItem(newItem);
        selectedItemContainer.getTreeItem().setExpanded(true);

        newItem.drawRepresentation(this.plotPane);
    }


    @FXML
    void addItemContainer() {
        Item selectedItem = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedItem instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedItem;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Creating Item");
            alert.setContentText("Cannot add an Item Container to an Item.");
            alert.show();
            return;
        }

        ItemContainer newItemContainer = new ItemContainer(
                "New_Item_Container" + this.numItems,
                0,
                new Point2D(0, 0),
                new Point3D(75, 75, 0)
        );
        this.numItems++;
        selectedItemContainer.addItem(newItemContainer);
        selectedItemContainer.getTreeItem().setExpanded(true);

        newItemContainer.drawRepresentation(plotPane);
    }

    @FXML
    void deleteSelected() {
        Item selectedItem = this.getSelectedItem();
        ItemContainer parent = selectedItem.getParent();
        if (parent == null) return;
        parent.removeItem(selectedItem);
        selectedItem.eraseRepresentation(plotPane);
        showSelectedItem();
    }

    @FXML
    void save() {
        Item selectedItem = this.getSelectedItem();
        double x, y, l, w, h;
        float price;
        if (selectedItem.getParent() == null) {
            return;
        }
        try {
            x = Double.parseDouble(xCoordTextBox.getText());
            y = Double.parseDouble(yCoordTextBox.getText());
            l = Double.parseDouble(lengthTextBox.getText());
            w = Double.parseDouble(widthTextBox.getText());
            h = Double.parseDouble(heightTextBox.getText());
            price = Float.parseFloat(priceTextBox.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error Saving");
            alert.setContentText("Please enter the correct value types for each text field.");
            alert.show();
            return;
        }
        selectedItem.getTreeItem().setValue(nameTextBox.getText());
        selectedItem.setName(nameTextBox.getText());
        selectedItem.setPosition(x, y);
        selectedItem.setSize(l, w, h);
        selectedItem.setPrice(price);

        selectedItem.eraseRepresentation(plotPane);
        selectedItem.drawRepresentation(plotPane);
        treeView.getSelectionModel().clearSelection();
        inputPane.setVisible(false);
    }

    @FXML
    void cancel() {
        treeView.getSelectionModel().clearSelection();
        inputPane.setVisible(false);
    }

    @FXML
    void showSelectedItem() {
        Item selectedItem = this.getSelectedItem();
        if (selectedItem.getParent() == null) {
            inputPane.setVisible(false);
        } else {
            inputPane.setVisible(true);
            nameTextBox.setText(selectedItem.getName());
            xCoordTextBox.setText(Double.toString(selectedItem.getPosition().getX()));
            yCoordTextBox.setText(Double.toString(selectedItem.getPosition().getY()));
            lengthTextBox.setText(Double.toString(selectedItem.getSize().getX()));
            widthTextBox.setText(Double.toString(selectedItem.getSize().getY()));
            heightTextBox.setText(Double.toString(selectedItem.getSize().getZ()));
            priceTextBox.setText(Double.toString(selectedItem.getPrice()));
        }
    }

    @FXML
    void initializeDrone() {
        //Init from selected item
        Item selectedItem = this.getSelectedItem();
        ItemContainer selectedItemContainer;
        if (selectedItem instanceof ItemContainer)
            selectedItemContainer = (ItemContainer) selectedItem;
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Error initializing drone");
            alert.setContentText("Please select a valid Item Container.");
            alert.show();
            return;
        }

        //Generate Command Center, add to Tree, set default position
        commandCenter = new ItemContainer(
                "Command Center",
                0,
                new Point2D(0, 0),
                new Point3D(100, 100, 0)
        );
        selectedItemContainer.addItem(commandCenter);
        selectedItemContainer.getTreeItem().setExpanded(true);
        commandCenter.drawRepresentation(this.plotPane);

        //Setup Drone (keep up with it individually)
        selectedItemContainer = commandCenter;
        drone = new Drone(
                "drone",
                0,
                new Point2D(0, 0),
                new Point3D(50, 50, 0),
                commandCenter
        );

        drone.getRepresentation().boundsInParentProperty();
        drone.drawRepresentation(this.plotPane);

        selectedItemContainer.addItem(drone);
        selectedItemContainer.getTreeItem().setExpanded(true);

        initDrone.setDisable(true);
        scanFarm.setDisable(false);
        visitItem.setDisable(false);
        returnHome.setDisable(false);
    }

    @FXML
    void runFarmScan() {
        scanFarm.setDisable(true);
        Timeline setup = drone.goToPoint(new Point2D(drone.getSize().getX(), drone.getSize().getY()));
        PathTransition scan = drone.farmScan(plotPane, null);

        setup.setOnFinished(e -> scan.playFromStart());
        scan.setOnFinished(e -> drone.goToItem(drone.getCommander(), f -> scanFarm.setDisable(false)));

        setup.playFromStart();
    }

    @FXML
    void goToItem() {
        Item selectedItem = this.getSelectedItem();
        if (selectedItem == farm) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Farm Dashboard Message");
            alert.setHeaderText("Drone Movement Error");
            alert.setContentText("Drone cannot visit the Farm.");
            alert.show();
            return;
        }

        visitItem.setDisable(true);
        drone.goToItem(selectedItem, e -> visitItem.setDisable(false));
    }

    @FXML
    void goBackHome() {
        returnHome.setDisable(true);
        drone.goToHome(e -> returnHome.setDisable(false));
    }
}

