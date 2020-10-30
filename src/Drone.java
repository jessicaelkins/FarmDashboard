package uab.cs420.farm_dashboard;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;


public class Drone extends Item {

    private final ItemContainer droneCommandCenter;

    Drone(String name, float price, Point2D position, Point3D size, ItemContainer commander) {
        super(name, price, position, size);
        ImageView image = new ImageView(new Image("src_drone.png"));
        image.setFitHeight(size.getY());
        image.setFitWidth(size.getX());
        image.setX(position.getX());
        image.setY(position.getY());
        super.setRepresentation(image);
        this.droneCommandCenter = commander;
    }

    public ItemContainer getCommander() {
        return droneCommandCenter;
    }

    public void setSize(Point3D dimensions) {
        super.setSize(dimensions);
        ImageView image = (ImageView) this.getRepresentation();
        image.setFitWidth(dimensions.getX());
        image.setFitHeight(dimensions.getY());
    }


    public void setjustPosition(Point2D point){
    	this.position = point;
    }


    public void setjustCenter(Point2D point) {
        this.setjustPosition(new Point2D(
                point.getX() - (this.getSize().getX() / 2),
                point.getY() - (this.getSize().getY() / 2)
        ));
    }

    public void setSize(double x, double y, double z) {
        this.setSize(new Point3D(x, y, z));
    }

    public PathTransition farmScan(Pane canvas, EventHandler<ActionEvent> onFinished) {
        final int sections = 8;
        final double increment = (canvas.getHeight() - (this.getSize().getY()* 2)) / sections;

        Path scan = new Path();

        double initX = this.getSize().getX();
        double initY = this.getSize().getY();

        scan.getElements().add(new MoveTo(initX, initY));

        final double WIDTH = canvas.getWidth() - (this.getSize().getX() * 1.5);
        for (int i = 0; i < sections; i++) {
            double currHeight = (i * increment) + initY;
            if (i % 2 == 0) {
                scan.getElements().add(new LineTo(WIDTH, currHeight));
                scan.getElements().add(new LineTo(WIDTH, currHeight + increment));
            } else {
                scan.getElements().add(new LineTo(initX, currHeight));
                scan.getElements().add(new LineTo(initX, currHeight + increment));
            }
            if (i == sections - 1) {
                scan.getElements().add(new LineTo(WIDTH, currHeight + increment));
            }
        }

        Duration scanTime = Duration.seconds(10);

        PathTransition farmScanAnim = new PathTransition();

        farmScanAnim.setNode(this.getRepresentation());
        farmScanAnim.setPath(scan);
        farmScanAnim.setDuration(scanTime);
        return farmScanAnim;
    }

    // Generate single translation to a point given by an item position
    // Returns Timeline so that said timeline could be added to sequence or
    // called after end of other animation
    public Timeline goToPoint(Point2D itemPosition) {
        Node representation = this.getRepresentation();
        representation.toFront();

        Duration startupDur = Duration.ZERO;
        Duration moveDur = Duration.seconds(1);

        KeyValue toPointStartVal = new KeyValue(representation.scaleXProperty(), 0);
        KeyFrame toPointStartFrame = new KeyFrame(startupDur, toPointStartVal);
        KeyValue toPointEndValX = new KeyValue(
                representation.translateXProperty(),
                itemPosition.getX() - (this.getSize().getX() / 2)
        );
        KeyValue toPointEndValY = new KeyValue(
                representation.translateYProperty(),
                itemPosition.getY() - (this.getSize().getY() / 2)
        );
        KeyFrame toPointEndFrame = new KeyFrame(moveDur, toPointEndValX, toPointEndValY);

        return new Timeline(toPointStartFrame, toPointEndFrame);
    }

    public void goToItem(Item toVisit, EventHandler<ActionEvent> onFinished) {
        Timeline itemMoveAnim = this.goToPoint(toVisit.getCenter());
        itemMoveAnim.setOnFinished(e -> {
            this.setjustCenter(toVisit.getCenter());
            onFinished.handle(e);
        });
        itemMoveAnim.playFromStart();
    }

    public void goToHome(EventHandler<ActionEvent> onFinished) {
        goToItem(this.droneCommandCenter, onFinished);
    }

}
