package a2;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class SnakeBody extends Circle {

    public static Color snakeBodyColor = Color.rgb(85, 120, 230);
    public int fruit = 0;

    public SnakeBody(double centerX, double centerY, double radius, Paint fill) {
        super(centerX, centerY, radius, fill);
    }

    public int getFruit() {
        return fruit;
    }

    public void setFruit(int fruitInd) {
        fruit = fruitInd;
    }
}
