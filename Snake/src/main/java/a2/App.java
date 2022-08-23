
package a2;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.nio.file.Paths;

public class App extends Application {


    public static final int sceneWidth = 1260;
    public static final int sceneHeight = 780;

    public static final int panelWidth = 270;

    public static final int blockWidth = 30;

    public static Media startBgm = new Media(Paths.get("bgm0.mp3").toUri().toString());
    public static MediaPlayer startBgmPlayer = new MediaPlayer(startBgm);

    public static Media lv1Bgm = new Media(Paths.get("bgm1.mp3").toUri().toString());
    public static MediaPlayer lv1BgmPlayer = new MediaPlayer(lv1Bgm);

    public static Media lv2Bgm = new Media(Paths.get("bgm2.mp3").toUri().toString());
    public static MediaPlayer lv2BgmPlayer = new MediaPlayer(lv2Bgm);

    public static Media lv3Bgm = new Media(Paths.get("bgm3.mp3").toUri().toString());
    public static MediaPlayer lv3BgmPlayer = new MediaPlayer(lv3Bgm);

    public static Media lv4Bgm = new Media(Paths.get("bgm4.mp3").toUri().toString());
    public static MediaPlayer lv4BgmPlayer = new MediaPlayer(lv4Bgm);


    // 0 plays startPage, 1 plays lv1, 2 plays lv2, 3plays lv3, 4 plays game over music
    public static void bgmPlay(int currentBg) {

        startBgmPlayer.pause();
        lv1BgmPlayer.pause();
        lv2BgmPlayer.pause();
        lv3BgmPlayer.pause();
        lv4BgmPlayer.pause();
        startBgmPlayer.seek(new Duration(0));
        lv1BgmPlayer.seek(Duration.seconds(1));
        lv2BgmPlayer.seek(new Duration(0));
        lv3BgmPlayer.seek(new Duration(0));
        lv4BgmPlayer.seek(new Duration(0));

        switch (currentBg) {
            case 0:
                startBgmPlayer.play();
                break;
            case 1:
                lv1BgmPlayer.play();
                break;
            case 2:
                lv2BgmPlayer.play();
                break;
            case 3:
                lv3BgmPlayer.play();
                break;
            case 4:
                lv4BgmPlayer.play();
                break;
        }
    }

    public static void bgmPauseUnpause(int currentBg, boolean pause) {

        if (pause) {
            startBgmPlayer.pause();
            lv1BgmPlayer.pause();
            lv2BgmPlayer.pause();
            lv3BgmPlayer.pause();
        } else {
            switch (currentBg) {
                case 0:
                    startBgmPlayer.play();
                    break;
                case 1:
                    lv1BgmPlayer.play();
                    break;
                case 2:
                    lv2BgmPlayer.play();
                    break;
                case 3:
                    lv3BgmPlayer.play();
                    break;
            }
        }
    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        startBgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        lv3BgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        lv4BgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);



        // Start page initialisation
        Pane startPage = new Pane();
        Image startImage = new Image("startPage1.png", 1260, 780, false, true);
        ImageView startImageView = new ImageView(startImage);
        Text pressStart = new Text("Press any key to start");
        pressStart.setFont(Font.font ("Verdana", 20));
        pressStart.setFill(Color.WHITE);
        pressStart.setX(750);
        pressStart.setY(730);
        startPage.getChildren().add(startImageView);
        startPage.getChildren().add(pressStart);
        FadeTransition ft = new FadeTransition(Duration.millis(2000), pressStart);
        ft.setFromValue(1);
        ft.setToValue(0.1);
        ft.setAutoReverse(true);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.play();


        // Game over page initialization
        Pane gameOverPage = new Pane();
        ImageView gameOverImage = new ImageView(new Image(
                "gameOverImage.png", 1280, 800, false, true));


        Text restartKey = new Text("Press R to restart the game");
        restartKey.setFont(Font.font ("Verdana", 28));
        restartKey.setFill(Color.rgb(124, 250, 76));
        restartKey.setX(450);
        restartKey.setY(700);

        FadeTransition restartft = new FadeTransition(Duration.millis(2000), restartKey);
        restartft.setFromValue(1);
        restartft.setToValue(0.1);
        restartft.setAutoReverse(true);
        restartft.setCycleCount(Animation.INDEFINITE);
        restartft.play();

        Text highScoreText = new Text("0");
        highScoreText.setFont(Font.font ("Verdana", 65));
        highScoreText.setFill(Color.rgb(124, 250, 76));
        highScoreText.setX(700);
        highScoreText.setY(580);

        gameOverPage.getChildren().add(gameOverImage);
        gameOverPage.getChildren().add(restartKey);
        gameOverPage.getChildren().add(highScoreText);

        // three backgrounds of the three levels
        Background bg1 = new Background("grid1.png", "grid2.png", "box2.png");
        Background bg2 = new Background("grass.png", "grid2.png", "box1.png");
        Background bg3 = new Background("grass.png", "flowers.png", "rock1.png");



        // Initialize the highest score
        Pane score = new Pane();

        Pane fruit = new Pane();

        Snake snake = new Snake();

        root.getChildren().add(bg1);

        root.getChildren().add(fruit);
        root.getChildren().add(snake);
        root.getChildren().add(score);
        root.getChildren().add(startPage);


        primaryStage.setTitle("The Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        bgmPlay(0);

        startImageView.requestFocus();
        startImageView.setOnKeyPressed(event -> {
            bgmPlay(1);
            root.getChildren().remove(4);
            snake.requestFocus();
            snake.startGame(root, startPage, gameOverPage, bg1, bg2, bg3, fruit, score);
        });
    }



    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
