package a2;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;




public class Background extends Canvas {

    public Background(String grid1, String grid2, String rockURL) {
        super(App.sceneWidth, App.sceneHeight);

        GraphicsContext gc = getGraphicsContext2D();

        Image grass1 = new Image(grid1, App.blockWidth, App.blockWidth, false, true);
        Image grass2 = new Image(grid2, App.blockWidth, App.blockWidth, false, true);
        Image rock = new Image(rockURL, App.blockWidth, App.blockWidth, false, true);

        for (int row = 0; row < App.sceneHeight; row += App.blockWidth) {
            for (int col = 0; col < App.sceneWidth; col += App.blockWidth) {
                if (row == 0 || row == App.sceneHeight - App.blockWidth || col == 0 ||
                        col == App.panelWidth || col == App.sceneWidth - App.blockWidth) {
                    gc.drawImage(rock, col, row);
                } else if ((row + col) / App.blockWidth % 2 == 0 && col >= App.panelWidth) {
                    gc.drawImage(grass2, col, row);
                } else if ((row + col) / App.blockWidth % 2 == 1 && col >= App.panelWidth) {
                    gc.drawImage(grass1, col, row);
                } else if ((row + col) / App.blockWidth % 2 == 0) {
                    gc.drawImage(grass2, col, row);
                } else if ((row + col) / App.blockWidth % 2 == 1) {
                    gc.drawImage(grass1, col, row);
                }
            }
        }
    }
}
