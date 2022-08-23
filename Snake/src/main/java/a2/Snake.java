package a2;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static javafx.scene.input.KeyCode.*;

public class Snake extends Group {


    Media crunch = new Media(Paths.get("appleCrunch.mp3").toUri().toString());
    MediaPlayer crunchPlayer = new MediaPlayer(crunch);

    Media dead = new Media(Paths.get("dead.wav").toUri().toString());
    MediaPlayer deadPlayer = new MediaPlayer(dead);

    private static KeyCode currentDir = RIGHT;
    private static KeyCode nextDir = RIGHT;

    private final Random rand = new Random();

    private int tempBodyLength = 0;

    private boolean gameOver = false;
    public boolean running = true;
    public boolean hasToRestart = false;

    public AnimationTimer timer;

    private int speed = 1;

    private int highestScore = 0;
    private int score = 0;

    private int countDown = 30;

    private ArrayList<Integer> los = new ArrayList<Integer>();

    Image headRight = new Image("headRight.png", App.blockWidth, App.blockWidth, false, true);
    Image eatRight = new Image("eatRight.png", App.blockWidth, App.blockWidth, false, true);

    Image headUp = new Image("headUp.png", App.blockWidth, App.blockWidth, false, true);
    Image eatUp = new Image("eatUp.png", App.blockWidth, App.blockWidth, false, true);

    Image headLeft = new Image("headLeft.png", App.blockWidth, App.blockWidth, false, true);
    Image eatLeft = new Image("eatLeft.png", App.blockWidth, App.blockWidth, false, true);

    Image headDown = new Image("headDown.png", App.blockWidth, App.blockWidth, false, true);
    Image eatDown = new Image("eatDown.png", App.blockWidth, App.blockWidth, false, true);

    Image figs = new Image("fig.png", 24, 24, false, true);
    Image blueBerry = new Image("Blueberry.png", 24, 24, false, true);
    Image dragonFruit = new Image("Dragonfruit.png", 24, 24, false, true);
    Image largeFigs = new Image("fig.png", 60, 60, false, true);
    Image largeBlueberry = new Image("Blueberry.png", 60, 60, false, true);
    Image largeDragonFruit = new Image("Dragonfruit.png", 60, 60, false, true);


    Image time = new Image("1.png", 50, 70, false, true);
    Image trophy = new Image("trophy.png", 80, 80, false, true);

    private SnakeHead snakeHead = new SnakeHead(headRight);

    private LinkedList<SnakeBody> snakeBody = new LinkedList<>();

    private ArrayList<ImageView> lof = new ArrayList<>();
    private ArrayList<Image> fruits = new ArrayList<>();

    public Snake() {
    }

    private void snakeBodyInit(LinkedList<SnakeBody> body) {
        for (int i = 0; i < 60; i++) {
            SnakeBody sb = new SnakeBody(snakeHead.getX() + 15 - i, 375, 10, SnakeBody.snakeBodyColor);
            snakeBody.add(sb);
        }
    }

    private void scorePaneInit(Pane paneScore) {

        ImageView t = new ImageView(time);
        ImageView trophies = new ImageView(trophy);
        ImageView fg = new ImageView(largeFigs);
        ImageView df = new ImageView(largeDragonFruit);
        ImageView bb = new ImageView(largeBlueberry);


        t.setX(45);
        t.setY(45);
        trophies.setX(30);
        trophies.setY(150);
        fg.setX(40);
        fg.setY(270);
        df.setX(40);
        df.setY(360);
        bb.setX(40);
        bb.setY(450);
        paneScore.getChildren().add(t);
        paneScore.getChildren().add(trophies);
        paneScore.getChildren().add(df);
        paneScore.getChildren().add(fg);
        paneScore.getChildren().add(bb);

        Text timeLeft = new Text(Integer.toString(countDown > 0 ? countDown : -countDown) +" s");
        timeLeft.setFont(Font.font(40));
        timeLeft.setX(160);
        timeLeft.setY(90);

        Text hs = new Text(Integer.toString(highestScore));
        hs.setFont(Font.font(40));
        hs.setX(160);
        hs.setY(210);

        Text fgs = new Text(Integer.toString(los.get(0)));
        fgs.setFont(Font.font(40));
        fgs.setX(160);
        fgs.setY(320);

        Text dfs = new Text(Integer.toString(los.get(1)));
        dfs.setFont(Font.font(40));
        dfs.setX(160);
        dfs.setY(410);

        Text bbs = new Text(Integer.toString(los.get(2)));
        bbs.setFont(Font.font(40));
        bbs.setX(160);
        bbs.setY(495);

        Text totalText = new Text("Total:");
        totalText.setFont(Font.font(40));
        totalText.setX(45);
        totalText.setY(570);

        Text totalScore = new Text(Integer.toString(score));
        totalScore.setFont(Font.font(40));
        totalScore.setX(160);
        totalScore.setY(570);

        paneScore.getChildren().add(timeLeft);
        paneScore.getChildren().add(hs);
        paneScore.getChildren().add(dfs);
        paneScore.getChildren().add(fgs);
        paneScore.getChildren().add(bbs);
        paneScore.getChildren().add(totalText);
        paneScore.getChildren().add(totalScore);

    }



    public void move(Group rt, Pane startPage, Pane gameOverPage, Background bg1,
                     Background bg2, Background bg3, Pane fruit, Pane paneScore) {
        if (gameOver) {
            Text hs = new Text(Integer.toString(highestScore));
            hs.setFont(Font.font(65));
            hs.setFill(Color.rgb(124, 250, 76));
            hs.setX(700);
            hs.setY(580);
            gameOverPage.getChildren().set(2, hs);

            if (!hasToRestart) {
                App.bgmPlay(4);
            }
            timer.stop();
            running = false;
            if (rt.getChildren().size() < 5)
                rt.getChildren().add(gameOverPage);
        }
        if (hasToRestart) {
            timer.stop();
            hasToRestart = false;
            gameOver = false;
            restartGame(rt, startPage, fruit, paneScore, bg1, timer);
        }
        switch (speed) {
            case 1:
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                rt.getChildren().set(0, bg1);
                break;
            case 2:
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                rt.getChildren().set(0, bg2);
                break;
            case 3:
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                moveOnePixel(rt, gameOverPage, paneScore);
                rt.getChildren().set(0, bg3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + speed);
        }
    }


    public void moveOnePixel(Group rt, Pane gameOverPage, Pane paneScore) {
        SnakeBody tempBody = snakeBody.removeLast();
        if (tempBody.getFruit() != 0) {
            if (tempBodyLength >= 0 && tempBodyLength <= 29) {
                SnakeBody grownBody = new SnakeBody(snakeHead.getX() + 15,
                        snakeHead.getY() + 15, 10, SnakeBody.snakeBodyColor);
                grownBody.setFruit(snakeHead.getFruit());
                getChildren().add(0, grownBody);
                snakeBody.addFirst(grownBody);
                snakeBody.addLast(tempBody);
                tempBodyLength ++;
            } else if (tempBodyLength == 30) {
                tempBody.setCenterX(snakeHead.getX() + 15);
                tempBody.setCenterY(snakeHead.getY() + 15);
                tempBody.setFruit(snakeHead.getFruit());
                tempBody.setRadius(10);
                snakeBody.addFirst(tempBody);
                tempBodyLength = 0;
            }

        } else {

            tempBody.setCenterX(snakeHead.getX() + 15);
            tempBody.setCenterY(snakeHead.getY() + 15);
            tempBody.setFruit(snakeHead.getFruit());
            tempBody.setRadius(10);
            snakeBody.addFirst(tempBody);

        }

        if (((snakeHead.getX() - App.panelWidth) % App.blockWidth == 0) &&
                (snakeHead.getY() % App.blockWidth == 0)) {
            currentDir = nextDir;
        }

        snakeHead.setFruit(0);
        boolean nearFruit = false;
        for (ImageView f : lof) {
            double x = snakeHead.getX() - f.getX();
            double y = snakeHead.getY() - f.getY();
            double sqrSum = x * x + y * y;
            if (sqrSum == 0) {
                crunchPlayer.seek(new Duration(0));
                crunchPlayer.play();
                snakeHead.setFruit(speed);
                los.set(speed - 1, los.get(speed - 1) + 1);
                score ++;
                if (score > highestScore) {
                    highestScore = score;
                }
                snakeBody.getFirst().setRadius(13);
                reset(f, speed, lof);
                updateScore(paneScore);
            }
            if (sqrSum < 3600) {
                nearFruit = true ;
            }
        }

        switch (currentDir) {
            case UP:
                if (snakeHead.getY() < App.blockWidth) {
                    gameOver = true;
                    deadPlayer.seek(Duration.seconds(0));
                    deadPlayer.play();
                    break;
                }
                snakeHead.setY(snakeHead.getY() - 1);
                snakeHead.setImage(nearFruit ? eatUp : headUp);
                break;
            case DOWN:
                if (snakeHead.getY() + 2 * App.blockWidth > App.sceneHeight) {
                    gameOver = true;
                    deadPlayer.seek(Duration.seconds(0));
                    deadPlayer.play();
                    break;
                }
                snakeHead.setY(snakeHead.getY() + 1);
                snakeHead.setImage(nearFruit ? eatDown : headDown);
                break;
            case LEFT:
                if (snakeHead.getX() < App.panelWidth + App.blockWidth) {
                    gameOver = true;
                    deadPlayer.seek(Duration.seconds(0));
                    deadPlayer.play();
                    break;
                }
                snakeHead.setX(snakeHead.getX() - 1);
                snakeHead.setImage(nearFruit ? eatLeft : headLeft);
                break;
            case RIGHT:
                if (snakeHead.getX()  + 2 * App.blockWidth > App.sceneWidth) {
                    gameOver = true;
                    deadPlayer.seek(Duration.seconds(0));
                    deadPlayer.play();
                    break;
                }
                snakeHead.setX(snakeHead.getX() + 1);
                snakeHead.setImage(nearFruit ? eatRight : headRight);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentDir);
        }

        // Self Collision
        for (Circle body : snakeBody) {

            double x = snakeHead.getX() + 15;
            double y = snakeHead.getY() + 15;
            switch (currentDir) {
                case UP:
                    if (snakeHead.getY() + 15 - 28 == body.getCenterY() &&
                            snakeHead.getX() + 15 == body.getCenterX()) {
                        gameOver = true;
                        deadPlayer.seek(Duration.seconds(0));
                        deadPlayer.play();
                        break;
                    }
                    break;
                case DOWN:
                    if (snakeHead.getY() + 15 + 28 == body.getCenterY() &&
                            snakeHead.getX() + 15 == body.getCenterX()) {
                        gameOver = true;
                        deadPlayer.seek(Duration.seconds(0));
                        deadPlayer.play();
                        break;
                    }
                    break;
                case LEFT:
                    if (snakeHead.getY() + 15 == body.getCenterY() &&
                            snakeHead.getX() + 15 - 28 == body.getCenterX()) {
                        gameOver = true;
                        deadPlayer.seek(Duration.seconds(0));
                        deadPlayer.play();
                        break;
                    }
                    break;
                case RIGHT:
                    if (snakeHead.getY() + 15 == body.getCenterY() &&
                            snakeHead.getX() + 15 + 28 == body.getCenterX()) {
                        gameOver = true;
                        deadPlayer.seek(Duration.seconds(0));
                        deadPlayer.play();
                        break;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currentDir);
            }
        }
    }

    private void reset(ImageView iv, int level, ArrayList<ImageView> lof) {
        switch (level) {
            case 1:
                iv.setImage(figs);
                break;
            case 2:
                iv.setImage(dragonFruit);
                break;
            case 3:
                iv.setImage(blueBerry);
                break;
        }
        Point2D pos = generatePoint(lof);
        iv.setX(pos.getX());
        iv.setY(pos.getY());
    }

    public Point2D generatePoint(ArrayList<ImageView> lof) {
        int intX = rand.nextInt(31) * App.blockWidth + App.panelWidth + App.blockWidth;
        int intY = rand.nextInt(24) * App.blockWidth + App.blockWidth;
        for (ImageView f : lof) {
            if (f.getX() == intX && f.getY() == intY) {
                return generatePoint(lof);
            }
        }
        return new Point2D(intX, intY);
    }

    private void refresh(ArrayList<ImageView> lof, int speed, Pane fruit) {
        ArrayList<ImageView> newList = new ArrayList<>();
        for (int j = 0; j < speed; j++) {
            for (int i = 0; i < 5; i++) {
                ImageView newFruit = new ImageView(fruits.get(speed - 1));
                newFruit.setX(900 + 30 * i);
                newFruit.setY(300 + 30 * j);
                newList.add(newFruit);
            }
        }
        lof.clear();
        lof.addAll(newList);
        fruit.getChildren().clear();
        fruit.getChildren().addAll(lof);
    }
    private void updateScore(Pane scorePane) {

        Text timeLeft = new Text(countDown > 0 ? countDown + " s" : "");
        timeLeft.setFont(Font.font(40));
        timeLeft.setX(160);
        timeLeft.setY(90);

        Text hs = new Text(Integer.toString(highestScore));
        hs.setFont(Font.font(40));
        hs.setX(160);
        hs.setY(210);

        Text fgs = new Text(Integer.toString(los.get(0)));
        fgs.setFont(Font.font(40));
        fgs.setX(160);
        fgs.setY(320);

        Text dfs = new Text(Integer.toString(los.get(1)));
        dfs.setFont(Font.font(40));
        dfs.setX(160);
        dfs.setY(410);

        Text bbs = new Text(Integer.toString(los.get(2)));
        bbs.setFont(Font.font(40));
        bbs.setX(160);
        bbs.setY(495);

        Text totalScore = new Text(Integer.toString(score));
        totalScore.setFont(Font.font(40));
        totalScore.setX(160);
        totalScore.setY(570);

        scorePane.getChildren().set(5, timeLeft);
        scorePane.getChildren().set(6, hs);
        scorePane.getChildren().set(7, dfs);
        scorePane.getChildren().set(8, fgs);
        scorePane.getChildren().set(9, bbs);
        scorePane.getChildren().set(11, totalScore);

    }

    public void startGame(Group rt, Pane startPage, Pane gameOverPage, Background bg1,
                          Background bg2, Background bg3, Pane fruit, Pane paneScore) {

        snakeBodyInit(snakeBody);
        getChildren().addAll(snakeBody);

        getChildren().add(snakeHead);

        los.add(0);
        los.add(0);
        los.add(0);
        fruits.add(figs);
        fruits.add(dragonFruit);
        fruits.add(blueBerry);
        refresh(lof, speed, fruit);
        scorePaneInit(paneScore);


        timer = new AnimationTimer() {
            long lastTime = 0;
            long oneSecond = 0;
            @Override
            public void handle(long now) {
                if (lastTime == 0) lastTime = now;
                if (oneSecond == 0) oneSecond = now;

                if ((now - oneSecond) / 1000 > 1000000) {
                    countDown -= 1;
                    updateScore(paneScore);
                    oneSecond = now;
                }

                if (now - lastTime > 20000) {
                    lastTime = now;
                    move(rt, startPage, gameOverPage, bg1, bg2, bg3, fruit, paneScore);
                }

                if (countDown == 0) {
                    if (speed == 1) {
                        App.bgmPlay(2);
                        speed = 2;
                        countDown = 30;
                        refresh(lof, speed, fruit);
                        updateScore(paneScore);
                    } else if (speed == 2) {
                        speed = 3;
                        App.bgmPlay(3);
                        refresh(lof, speed, fruit);
                        updateScore(paneScore);
                    }
                }


            }
        };

        setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();
            switch (key) {
                case LEFT:
                    switch (currentDir) {
                        case LEFT:
                            nextDir = DOWN;
                            break;
                        case DOWN:
                            nextDir = RIGHT;
                            break;
                        case RIGHT:
                            nextDir = UP;
                            break;
                        case UP:
                            nextDir = LEFT;
                            break;
                    }
                    break;
                case RIGHT:
                    switch (currentDir) {
                        case LEFT:
                            nextDir = UP;
                            break;
                        case DOWN:
                            nextDir = LEFT;
                            break;
                        case RIGHT:
                            nextDir = DOWN;
                            break;
                        case UP:
                            nextDir = RIGHT;
                            break;
                    }
                    break;
                case DIGIT1:
                    App.bgmPlay(1);
                    speed = 1;
                    countDown = 30;
                    refresh(lof, speed, fruit);
                    updateScore(paneScore);
                    break;
                case DIGIT2:
                    App.bgmPlay(2);
                    speed = 2;
                    countDown = 30;
                    refresh(lof, speed, fruit);
                    updateScore(paneScore);
                    break;
                case DIGIT3:
                    App.bgmPlay(3);
                    speed = 3;
                    countDown = -1;
                    refresh(lof, speed, fruit);
                    updateScore(paneScore);
                    break;
                case P:
                    App.bgmPauseUnpause(speed, running);
                    running = !running;
                    if (running) {
                        timer.start();
                    } else {
                        timer.stop();
                    }
                    break;
                case R:
                    gameOver = true;
                    hasToRestart = true;
                    timer.start();
                    running = true;
                    break;
                case Q:
                    gameOver = true;
                    break;
            }
        });
        timer.start();
    }


    public void restartGame(Group root, Pane startPage, Pane fruit, Pane paneScore, Background bg1, AnimationTimer timer) {

        App.bgmPlay(0);
        root.getChildren().remove(4);

        snakeBody.clear();
        snakeHead = new SnakeHead(headRight);
        snakeBodyInit(snakeBody);
        getChildren().clear();
        getChildren().addAll(snakeBody);
        getChildren().add(snakeHead);
        los.set(0, 0);
        los.set(1, 0);
        los.set(2, 0);
        score = 0;
        speed = 1;
        countDown = 30;

        currentDir = RIGHT;
        nextDir = RIGHT;

        root.getChildren().set(0, bg1);
        refresh(lof, speed, fruit);
        updateScore(paneScore);

        root.getChildren().add(startPage);
        tempBodyLength = 0;

        paneScore.requestFocus();
        paneScore.setOnKeyPressed(event -> {
            App.bgmPlay(1);
            root.getChildren().remove(4);
            gameOver = false;
            hasToRestart = false;
            running = true;
            requestFocus();
            timer.start();
        });
    }
}
