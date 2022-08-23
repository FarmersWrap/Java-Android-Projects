package a2;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SnakeHead extends ImageView {

    private int fruit = 0;

    public SnakeHead(Image image) {
        super(image);
        setX(780);
        setY(360);
    }



    public int getFruit() {
        return fruit;
    }

    public void setFruit(int fruitIndex) {
        fruit = fruitIndex;
    }

}
