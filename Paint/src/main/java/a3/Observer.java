package a3;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface Observer {
    void notified();
    void translate(double displacementX, double displacementY);
    void setSize(double endX, double endY);

    Color getLineColor();
    Color getFillColor();
    double getLineWidth();
    int getLineStyle();
    String toString();

    double getIndicatorX();

    double getIndicatorY();

    double getToolbarX();

    double getToolbarY();
}
