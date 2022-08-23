package a3;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

import static java.lang.Math.sqrt;
import static java.lang.System.exit;

public class App extends Application {

    final double STAGE_WIDTH = 1280;
    final double STAGE_HEIGHT = 760;

    // code provided in samples
    final double BUTTON_MIN_WIDTH = 50;
    final double BUTTON_PREF_WIDTH = 100;
    final double BUTTON_MAX_WIDTH = 200;

    final double tbx = 30;
    final double tby = 105;

    final String DELIMITER = ",";
    final String ENDL = "\n";

    final int iconSize = 20;

    Image selectImage = new Image("select.png", iconSize, iconSize, true, true);
    Image eraseImage = new Image("eraser.png", iconSize, iconSize, true, true);
    Image fillImage = new Image("fill.png", iconSize, iconSize, true, true);
    Image lineImage = new Image("line.png", iconSize, iconSize, true, true);
    Image circleImage = new Image("circle.png", iconSize, iconSize, true, true);
    Image rectImage = new Image("rect.png", iconSize, iconSize, true, true);

    Paint paint;
    Circle dot1 = new Circle(6, 6, 4, Color.RED);
    Circle dot2 = new Circle(6, 6, 4, Color.RED);

    int shapeIdx = -1;
    boolean saved = true;

    private class SketchItMenu extends MenuBar {

        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu helpMenu = new Menu("Help");
        MenuItem itemNew = new MenuItem("New");
        MenuItem itemLoad = new MenuItem("Load");
        MenuItem itemSave = new MenuItem("Save");
        MenuItem itemQuit = new MenuItem("Quit");
        MenuItem itemCut = new MenuItem("Cut");
        MenuItem itemCopy = new MenuItem("Copy");
        MenuItem itemPaste = new MenuItem("Paste");
        MenuItem itemAbout = new MenuItem("About");

        SketchItMenu() {
            super();
            getMenus().add(fileMenu);
            getMenus().add(editMenu);
            getMenus().add(helpMenu);
            fileMenu.getItems().add(itemNew);
            fileMenu.getItems().add(itemLoad);
            fileMenu.getItems().add(itemSave);
            fileMenu.getItems().add(itemQuit);

            editMenu.getItems().add(itemCut);
            editMenu.getItems().add(itemCopy);
            editMenu.getItems().add(itemPaste);

            helpMenu.getItems().add(itemAbout);
        }

        public MenuItem getItemCut() {
            return itemCut;
        }

        public MenuItem getItemCopy() {
            return itemCopy;
        }

        public MenuItem getItemPaste() {
            return itemPaste;
        }

        public MenuItem getItemNew() {
            return itemNew;
        }

        public MenuItem getItemLoad() {
            return itemLoad;
        }

        public MenuItem getItemSave() {
            return itemSave;
        }

        public MenuItem getItemQuit() {
            return itemQuit;
        }

        public MenuItem getItemAbout() {
            return itemAbout;
        }
    }


    // code provided in samples
    private class StandardButton extends ToggleButton {
        StandardButton(String caption, Node n) {
            super(caption, n);
            setVisible(true);
            setMinWidth(BUTTON_MIN_WIDTH);
            setPrefWidth(BUTTON_PREF_WIDTH);
            setMaxWidth(BUTTON_MAX_WIDTH);


            setMinWidth(BUTTON_MIN_WIDTH);
            setPrefWidth(BUTTON_PREF_WIDTH);
            setMaxWidth(BUTTON_MAX_WIDTH);
        }
    }


    private class SketchItToolPalette extends VBox {
        Group s;
        Group indic;
        SketchItMenu SIM = new SketchItMenu();
        // Buttons integrated in the VBox (select, erase, line, circle, rect, fill)
        ToggleButton select = new StandardButton("Select", new ImageView(selectImage));
        ToggleButton erase = new StandardButton("Erase", new ImageView(eraseImage));
        ToggleButton drawLine = new StandardButton("Line", new ImageView(lineImage));
        ToggleButton drawCircle = new StandardButton("Circle", new ImageView(circleImage));
        ToggleButton drawRectangle = new StandardButton("Rectangle", new ImageView(rectImage));
        ToggleButton fill = new StandardButton("Fill", new ImageView(fillImage));
        ToggleGroup toolPicker = new ToggleGroup();

        // line color and fill color
        ColorPicker cpLine = new ColorPicker(Color.BLACK);
        ColorPicker cpFill = new ColorPicker(Color.WHITE);
        Label lineLabel = new Label("Line Color", cpLine);
        Label fillLabel = new Label("Fill Color", cpFill);

        // line width
        Line lineThickness = new Line();
        Label strokeWidthLabel = new Label("stroke width", lineThickness);
        Slider slider = new Slider();

        // line style
        Label lineStyle = new Label("Line Style");
        Line style0Line = new Line();
        Line style1Line = new Line();
        Line style2Line = new Line();
        Line style3Line = new Line();
        StandardButton ToggleButton0 = new StandardButton("style 0", style0Line);
        StandardButton ToggleButton1 = new StandardButton("style 1", style1Line);
        StandardButton ToggleButton2 = new StandardButton("style 2", style2Line);
        StandardButton ToggleButton3 = new StandardButton("style 3", style3Line);

        ArrayList<ToggleButton> styleList = new ArrayList<ToggleButton>();
        ArrayList<Line> styleLineList = new ArrayList<Line>();
        int curStyle = 0;
        ToggleGroup linePicker = new ToggleGroup();

        double startX = 0;
        double startY = 0;
        double endX = 0;
        double endY = 0;

        double clickedX = 0;
        double clickedY = 0;

        ArrayList<Observer> list = new ArrayList<Observer>();

        public ArrayList<Observer> getTheList() {
            return list;
        }

        private class SketchLine extends Line implements Observer {
            double displacementX = 0;
            double displacementY = 0;
            int lineStyle = 0;

            SketchLine(double startX, double startY, double endX, double endY) {
                super(startX, startY, endX, endY);
                notified();
            }

            public double getDisplacementX() {
                return displacementX;
            }
            public double getDisplacementY() {
                return displacementY;
            }

            public void notified() {
                lineStyle = curStyle;
                styleList.get(lineStyle).setSelected(true);
                setStroke(cpLine.getValue());
                setStrokeWidth(slider.getValue());
                getStrokeDashArray().setAll(styleLineList.get(curStyle).getStrokeDashArray());
                saved = false;
            }
            public void translate(double sX, double sY) {
                setStartX(sX);
                setStartY(sY);
                setEndX(sX + this.getDisplacementX());
                setEndY(sY + this.getDisplacementY());
            }
            public void setSize(double endX, double endY) {
                setEndX(endX);
                setEndY(endY);
                displacementX = endX - startX;
                displacementY = endY - startY;
            }
            public Color getLineColor() {
                return (Color)getStroke();
            }
            public Color getFillColor() {
                return (Color)getStroke();
            }
            public double getLineWidth() {
                return getStrokeWidth();
            }
            public int getLineStyle() {
                return lineStyle;
            }

            public String toString() {
                String out = "0" + DELIMITER + displacementX + DELIMITER + displacementY + DELIMITER +
                        getLineStyle() + DELIMITER + getStartX() + DELIMITER + getStartY() +
                        DELIMITER + getEndX() + DELIMITER + getEndY() + DELIMITER +
                        cpLine.getValue().getRed() + DELIMITER +
                        cpLine.getValue().getGreen() + DELIMITER +
                        cpLine.getValue().getBlue() + DELIMITER +
                        cpFill.getValue().getRed() + DELIMITER +
                        cpFill.getValue().getGreen() + DELIMITER +
                        cpFill.getValue().getBlue() + DELIMITER +
                        slider.getValue() + ENDL;
                return out;
            }

            @Override
            public double getIndicatorX() {
                return getStartX();
            }

            @Override
            public double getIndicatorY() {
                return getStartY();
            }

            @Override
            public double getToolbarX() {
                return tbx;
            }

            @Override
            public double getToolbarY() {
                return tby;
            }

        }

        private class SketchCircle extends Circle implements Observer {
            int lineStyle = 0;
            public SketchCircle(double startX, double startY, int i, Color value) {
                super(startX, startY, i, value);
                notified();
            }
            public void notified() {
                lineStyle = curStyle;
                styleList.get(lineStyle).setSelected(true);
                setStroke(cpLine.getValue());
                setFill(cpFill.getValue());
                setStrokeWidth(slider.getValue());
                getStrokeDashArray().setAll(styleLineList.get(curStyle).getStrokeDashArray());
                saved = false;
            }


            @Override
            // final position of the center
            public void translate(double displacementX, double displacementY) {
                setCenterX(displacementX);
                setCenterY(displacementY);
            }
            @Override
            public void setSize(double endX, double endY) {
                setRadius(sqrt((endX - getCenterX()) * (endX - getCenterX()) +
                        (endY - getCenterY()) * (endY - getCenterY())));
            }
            public Color getLineColor() {
                return (Color)getStroke();
            }
            public Color getFillColor() {
                return (Color)getFill();
            }
            public double getLineWidth() {
                return getStrokeWidth();
            }
            public int getLineStyle() {
                return lineStyle;
            }
            public String toString() {
                String out = "1" + DELIMITER + "0" + DELIMITER + "0" + DELIMITER +
                        getLineStyle() + DELIMITER + getCenterX() + DELIMITER + getCenterY() +
                        DELIMITER + getRadius() + DELIMITER + "0" + DELIMITER +
                        cpLine.getValue().getRed() + DELIMITER +
                        cpLine.getValue().getGreen() + DELIMITER +
                        cpLine.getValue().getBlue() + DELIMITER +
                        cpFill.getValue().getRed() + DELIMITER +
                        cpFill.getValue().getGreen() + DELIMITER +
                        cpFill.getValue().getBlue() + DELIMITER +
                        slider.getValue() + ENDL;
                return out;
            }

            @Override
            public double getIndicatorX() {
                return getCenterX();
            }

            @Override
            public double getIndicatorY() {
                return getCenterY();
            }

            @Override
            public double getToolbarX() {
                return tbx;
            }

            @Override
            public double getToolbarY() {
                return tby + 30;
            }
        }


        private class SketchRect extends Rectangle implements Observer {
            int lineStyle = 0;
            public SketchRect(double startX, double startY, int width, int height) {
                super(startX, startY, width, height);
                notified();
            }
            public void notified() {
                lineStyle = curStyle;
                styleList.get(lineStyle).setSelected(true);
                setStroke(cpLine.getValue());
                setFill(cpFill.getValue());
                setStrokeWidth(slider.getValue());
                getStrokeDashArray().setAll(styleLineList.get(curStyle).getStrokeDashArray());
                saved = false;
            }
            public void translate(double sX, double sY) {
                setX(sX);
                setY(sY);
            }
            public void setSize(double endX, double endY) {
                setWidth(endX - getX());
                setHeight(endY - getY());
            }
            public Color getLineColor() {
                return (Color)getStroke();
            }
            public Color getFillColor() {
                return (Color)getFill();
            }
            public double getLineWidth() {
                return getStrokeWidth();
            }
            public int getLineStyle() {
                return lineStyle;
            }
            public String toString() {
                String out = "2" + DELIMITER + "0" + DELIMITER + "0" + DELIMITER +
                        getLineStyle() + DELIMITER + getX() + DELIMITER + getY() +
                        DELIMITER + getWidth() + DELIMITER + getHeight() + DELIMITER +
                        cpLine.getValue().getRed() + DELIMITER +
                        cpLine.getValue().getGreen() + DELIMITER +
                        cpLine.getValue().getBlue() + DELIMITER +
                        cpFill.getValue().getRed() + DELIMITER +
                        cpFill.getValue().getGreen() + DELIMITER +
                        cpFill.getValue().getBlue() + DELIMITER +
                        slider.getValue() + ENDL;
                return out;
            }

            @Override
            public double getIndicatorX() {
                return getX();
            }

            @Override
            public double getIndicatorY() {
                return getY();
            }

            @Override
            public double getToolbarX() {
                return tbx;
            }

            @Override
            public double getToolbarY() {
                return tby + 60;
            }

        }

        public Observer oFactory(String s) {
            String[] values;
            values = s.split(DELIMITER);
            int shapeType = Integer.parseInt(values[0]);
            double disX = Double.parseDouble(values[1]);
            double disY = Double.parseDouble(values[2]);
            int lineStyle = Integer.parseInt(values[3]);

            double startX = Double.parseDouble(values[4]);
            double startY = Double.parseDouble(values[5]);
            double endX = Double.parseDouble(values[6]);
            double endY = Double.parseDouble(values[7]);

            double lr = Double.parseDouble(values[8]);
            double lg = Double.parseDouble(values[9]);
            double lb = Double.parseDouble(values[10]);

            double fr = Double.parseDouble(values[11]);
            double fg = Double.parseDouble(values[12]);
            double fb = Double.parseDouble(values[13]);

            double slider = Double.parseDouble(values[14]);

            if (shapeType == 0) {
                SketchLine l = new SketchLine(startX, startY, endX, endY);
                l.lineStyle = lineStyle;
                l.displacementX = disX;
                l.displacementY = disY;
                l.setStroke(Color.color(lr, lg, lb));
                l.setStrokeWidth(slider);
                l.getStrokeDashArray().setAll(styleLineList.get(lineStyle).getStrokeDashArray());
                return l;
            } else if (shapeType == 1) {
                SketchCircle c = new SketchCircle(startX, startY, 5, Color.color(fr, fg, fb));
                c.lineStyle = lineStyle;
                c.setRadius(endX);
                c.setStroke(Color.color(lr, lg, lb));
                c.setStrokeWidth(slider);
                c.setFill(Color.color(fr, fg, fb));
                c.getStrokeDashArray().setAll(styleLineList.get(lineStyle).getStrokeDashArray());
                return c;
            } else if (shapeType == 2) {
                SketchRect rect = new SketchRect(startX, startY, 2, 3);
                rect.setWidth(endX);
                rect.setHeight(endY);
                rect.lineStyle = lineStyle;
                rect.setStroke(Color.color(lr, lg, lb));
                rect.setStrokeWidth(slider);
                rect.setFill(Color.color(fr, fg, fb));
                rect.getStrokeDashArray().setAll(styleLineList.get(lineStyle).getStrokeDashArray());
                return rect;
            }
            return null;
        }
        public SketchItMenu getSIM() {
            return SIM;
        }
        SketchItToolPalette(Group shapes, Group indicater) throws Exception {
            super();
            s = shapes;
            indic = indicater;
            dot1.setStroke(Color.BLUE);
            dot1.setStrokeWidth(2);
            dot2.setStroke(Color.BLUE);
            dot2.setStrokeWidth(2);

            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    "Please save if you did not save...", ButtonType.YES);
            Alert b = new Alert(Alert.AlertType.INFORMATION,
                    "Program Name: SketchIt\nProgrammer: Zhenglin Yu\nWatID: 20734958", ButtonType.CLOSE);
            setOpacity(1);
            setLayoutX(5);

            SIM.getItemNew().setOnAction(event -> {
                if (!saved) {
                    Stage stage = new Stage();
                    TextArea text = new TextArea("I will use a new file without saving!");
                    text.setWrapText(true);
                    text.setPrefWidth(280);
                    text.setPrefHeight(125);
                    text.relocate(10, 10);
                    text.setEditable(false);

                    Button ok = new Button("Ok");
                    ok.setPrefWidth(75);
                    ok.relocate(130, 155);

                    Button cancel = new Button("Cancel");
                    cancel.setPrefWidth(75);
                    cancel.relocate(215, 155);

                    Scene scene = new Scene(new Pane(
                            text, ok, cancel), 300, 200);
                    stage.setScene(scene);
                    stage.setTitle("Dialog Box");
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);
                    stage.show();

                    cancel.setOnAction(event1 -> {
                        stage.close();
                    });
                    ok.setOnAction(event1 -> {
                        shapeIdx = -1;
                        changeIndicator(indicater);
                        list.clear();
                        shapes.getChildren().clear();
                        stage.close();
                    });
                } else {
                    shapeIdx = -1;
                    changeIndicator(indicater);
                    list.clear();
                    shapes.getChildren().clear();
                }
            });
            SIM.getItemQuit().setOnAction(event -> {
                if (!saved) {
                    Stage stage = new Stage();
                    TextArea text = new TextArea("I will leave without saving!");
                    text.setWrapText(true);
                    text.setPrefWidth(280);
                    text.setPrefHeight(125);
                    text.relocate(10, 10);
                    text.setEditable(false);

                    Button ok = new Button("Ok");
                    ok.setPrefWidth(75);
                    ok.relocate(130, 155);

                    Button cancel = new Button("Cancel");
                    cancel.setPrefWidth(75);
                    cancel.relocate(215, 155);

                    Scene scene = new Scene(new Pane(
                            text, ok, cancel), 300, 200);
                    stage.setScene(scene);
                    stage.setTitle("Dialog Box");
                    stage.setResizable(false);
                    stage.setAlwaysOnTop(true);
                    stage.show();

                    cancel.setOnAction(event1 -> {
                        stage.close();
                    });
                    ok.setOnAction(event1 -> {
                        exit(0);
                        stage.close();
                    });


                } else {
                    exit(0);
                }
            });


            SIM.getItemSave().setOnAction(event -> {

                FileWriter file = null;
                BufferedWriter writer = null;
                try {
                    file = new FileWriter("data.txt");
                    writer = new BufferedWriter(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        writer.write(
                                list.get(i).toString()
                        );
                    }
                    writer.close();
                    file.close();
                    saved = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            if (!saved) {
                a.showAndWait();
            } else {
                shapeIdx = -1;
                changeIndicator(indicater);
                list.clear();
                shapes.getChildren().clear();
            }


            SIM.getItemLoad().setOnAction(event -> {


                Stage stage = new Stage();
                TextArea text = new TextArea("Current picture is going to be deleted.");
                text.setWrapText(true);
                text.setPrefWidth(280);
                text.setPrefHeight(125);
                text.relocate(10, 10);
                text.setEditable(false);

                Button ok = new Button("Ok");
                ok.setPrefWidth(75);
                ok.relocate(130, 155);

                Button cancel = new Button("Cancel");
                cancel.setPrefWidth(75);
                cancel.relocate(215, 155);

                Scene scene = new Scene(new Pane(
                        text, ok, cancel), 300, 200);
                stage.setScene(scene);
                stage.setTitle("Dialog Box");
                stage.setResizable(false);
                stage.setAlwaysOnTop(true);
                stage.show();

                cancel.setOnAction(event1 -> {
                    stage.close();
                });
                ok.setOnAction(event1 -> {

                    shapeIdx = -1;
                    changeIndicator(indicater);
                    list.clear();
                    shapes.getChildren().clear();
                    FileReader file = null;
                    BufferedReader reader = null;
                    try {
                        file = new FileReader("data.txt");
                        reader = new BufferedReader(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Observer l = oFactory(line);
                            list.add(l);
                            shapes.getChildren().add((Node) l);
                        }
                        saved = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stage.close();
                });


            });

            Alert cut = new Alert(Alert.AlertType.INFORMATION,
                    "Nothing is selected", ButtonType.OK);

            SIM.getItemCut().setOnAction(event -> {
                if (shapeIdx >= 0) {
                    FileWriter file = null;
                    BufferedWriter writer = null;
                    try {
                        file = new FileWriter("copy.txt");
                        writer = new BufferedWriter(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        writer.write(
                                list.get(shapeIdx).toString()
                        );
                        writer.close();
                        file.close();
                        list.remove(shapeIdx);
                        shapes.getChildren().remove(shapeIdx);
                        shapeIdx = -1;
                        changeIndicator(indicater);
                        saved = false;
                        cut.setContentText("Cut succeed!");
                        cut.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    cut.setContentText("Nothing is selected");
                    cut.showAndWait();
                }
            });

            SIM.getItemCopy().setOnAction(event -> {
                if (shapeIdx >= 0) {
                    FileWriter file = null;
                    BufferedWriter writer = null;
                    try {
                        file = new FileWriter("copy.txt");
                        writer = new BufferedWriter(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        writer.write(
                                list.get(shapeIdx).toString()
                        );
                        writer.close();
                        file.close();
                        cut.setContentText("Copy succeed!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        cut.showAndWait();
                    }
                } else {
                    cut.setContentText("Nothing is selected");
                    cut.showAndWait();
                }

            });

            SIM.getItemPaste().setOnAction(event -> {
                FileReader file = null;
                BufferedReader reader = null;
                try {
                    file = new FileReader("copy.txt");
                    reader = new BufferedReader(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Observer l = oFactory(line);
                        list.add(l);
                        shapes.getChildren().add((Node) l);
                    }
                    saved = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cut.setContentText("If you copied or cut something,\n" +
                        "    a copy was pasted to its original position.\n" +
                        "Otherwise, nothing was pasted.");
                cut.showAndWait();
            });



            SIM.getItemAbout().setOnAction(event -> {
                b.showAndWait();
            });

            select.setToggleGroup(toolPicker);
            erase.setToggleGroup(toolPicker);
            drawLine.setToggleGroup(toolPicker);
            drawCircle.setToggleGroup(toolPicker);
            drawRectangle.setToggleGroup(toolPicker);
            fill.setToggleGroup(toolPicker);
            select.setSelected(true);

            erase.setOnMouseClicked(event -> {
                shapeIdx = -1;
                changeIndicator(indicater);
            });

            drawLine.setOnMouseClicked(event -> {
                shapeIdx = -1;
                changeIndicator(indicater);
            });

            drawCircle.setOnMouseClicked(event -> {
                shapeIdx = -1;
                changeIndicator(indicater);
            });

            drawRectangle.setOnMouseClicked(event -> {
                shapeIdx = -1;
                changeIndicator(indicater);
            });

            fill.setOnMouseClicked(event -> {
                shapeIdx = -1;
                changeIndicator(indicater);
            });

            cpLine.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    if (shapeIdx >= 0) {
                        list.get(shapeIdx).notified();
                    }
                }
            });

            cpFill.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    if (shapeIdx >= 0) {
                        list.get(shapeIdx).notified();
                    }
                }
            });


            lineThickness.setStrokeWidth(5.0);
            lineThickness.setStartX(0);
            lineThickness.setEndX(BUTTON_PREF_WIDTH);

            slider.setMin(2);
            slider.setMax(20);
            slider.setValue(5);
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number > observable,
                                    Number oldValue, Number newValue) {
                    lineThickness.setStrokeWidth(slider.getValue());
                    if (shapeIdx >= 0) {
                        list.get(shapeIdx).notified();
                    }
                }
            });


            styleLineList.add(style0Line);
            styleLineList.add(style1Line);
            styleLineList.add(style2Line);
            styleLineList.add(style3Line);
            for (int i = 0; i < 4; i ++) {
                styleLineList.get(i).setStrokeWidth(3.0);
                styleLineList.get(i).setStartX(0);
                styleLineList.get(i).setEndX(BUTTON_PREF_WIDTH);
            }
            style1Line.getStrokeDashArray().addAll(50d, 40d, 10d, 40d);
            style2Line.getStrokeDashArray().addAll(60d, 25d, 60d, 25d);
            style3Line.getStrokeDashArray().addAll(20d, 25d, 20d, 25d);

            ToggleButton0.setToggleGroup(linePicker);
            ToggleButton1.setToggleGroup(linePicker);
            ToggleButton2.setToggleGroup(linePicker);
            ToggleButton3.setToggleGroup(linePicker);
            ToggleButton0.setSelected(true);
            curStyle = 0;
            styleList.add(ToggleButton0);
            styleList.add(ToggleButton1);
            styleList.add(ToggleButton2);
            styleList.add(ToggleButton3);
            for (int i = 0; i < 4; i++) {
                styleList.get(i).setOnMouseReleased(event -> {
                    for (int j = 0; j < 4; j++) {
                        if (styleList.get(j).isSelected()) {
                            curStyle = j;
                            break;
                        }
                    }
                    if (shapeIdx >= 0) {
                        list.get(shapeIdx).notified();
                    }
                });
            }

            getChildren().addAll(select, erase, drawLine, drawCircle,
                    drawRectangle, fill, new Separator(Orientation.HORIZONTAL),
                    cpLine, lineLabel,new Separator(Orientation.HORIZONTAL),
                    cpFill, fillLabel,new Separator(Orientation.HORIZONTAL),
                    strokeWidthLabel, slider,new Separator(Orientation.HORIZONTAL),
                    lineStyle, ToggleButton0, ToggleButton1, ToggleButton2, ToggleButton3);

        }


        public ToggleButton getSelectButton() {
            return select;
        }

        int findShape(double x, double y, Group shapes) {
            for (int i = shapes.getChildren().size() - 1; i >= 0; i --) {
                if (shapes.getChildren().get(i).contains(x, y)) return i;
            }
            return -1;
        }

        public void setStyleButton(int buttonNumber) {
            styleList.get(buttonNumber).setSelected(true);
            curStyle = buttonNumber;
        }

        public void changeIndicator(Group indicators) {
            indicators.getChildren().clear();
            if (shapeIdx >= 0) {
                dot1.setCenterX(list.get(shapeIdx).getIndicatorX());
                dot1.setCenterY(list.get(shapeIdx).getIndicatorY());
                dot2.setCenterX(list.get(shapeIdx).getToolbarX());
                dot2.setCenterY(list.get(shapeIdx).getToolbarY());
                indicators.getChildren().add(dot1);
                indicators.getChildren().add(dot2);
            }

        }

        public void mousePressedHandler(MouseEvent event, Group shapes, Group indicators) {
            startX = event.getX();
            endX = event.getX();
            startY = event.getY();
            endY = event.getY();
            clickedX = event.getX();
            clickedY = event.getX();

            shapeIdx = findShape(event.getX(), event.getY(), shapes);
            if (shapeIdx == -1) changeIndicator(indicators);
            // No such shape found
            if (select.isSelected()) {
                changeIndicator(indicators);
                if (shapeIdx == -1) {
                    select.setSelected(false);
                    return;
                }
                cpLine.setValue(list.get(shapeIdx).getLineColor());
                cpFill.setValue(list.get(shapeIdx).getFillColor());
                slider.setValue(list.get(shapeIdx).getLineWidth());
                setStyleButton(list.get(shapeIdx).getLineStyle());
                saved = false;
            } else if (erase.isSelected()) {
                if (shapeIdx == -1) return;
                list.remove(shapeIdx);
                shapes.getChildren().remove(shapeIdx);
                shapeIdx = -1;
                saved = false;
                changeIndicator(indicators);
            } else if (drawLine.isSelected()) {
                SketchLine newLine = new SketchLine(startX, startY, endX, endY);
                newLine.notified();
                shapeIdx = list.size();
                list.add(newLine);
                shapes.getChildren().add(newLine);
                saved = false;
                changeIndicator(indicators);
            } else if (drawCircle.isSelected()) {
                SketchCircle newCircle = new SketchCircle(startX, startY, 5, cpFill.getValue());
                newCircle.notified();
                shapeIdx = list.size();
                list.add(newCircle);
                shapes.getChildren().add(newCircle);
                saved = false;
                changeIndicator(indicators);
            } else if (drawRectangle.isSelected()) {
                SketchRect newRect = new SketchRect(startX, startY, 10, 5);
                newRect.notified();
                shapeIdx = list.size();
                list.add(newRect);
                shapes.getChildren().add(newRect);
                saved = false;
                changeIndicator(indicators);
            } else if (fill.isSelected()) {
                if (shapeIdx == -1) return;
                list.get(shapeIdx).notified();
                saved = false;
                shapeIdx = -1;
                changeIndicator(indicators);
            }
        }

        public void mouseDraggedHandler(MouseEvent event, Group indicators) {
             if (shapeIdx >= 0) {
                 if (select.isSelected()) {
                     list.get(shapeIdx).translate(event.getX(), event.getY());
                     changeIndicator(indicators);
                  } else if (drawLine.isSelected() || drawCircle.isSelected() || drawRectangle.isSelected()) {
                     list.get(shapeIdx).setSize(event.getX(), event.getY());
                     changeIndicator(indicators);
                 }
             }
        }

        public void mouseReleasedHandler(MouseEvent event, Group indicators) {
            if (shapeIdx >= 0) {
                if (drawLine.isSelected() || drawCircle.isSelected() || drawRectangle.isSelected()) {
                    drawLine.setSelected(false);
                    drawCircle.setSelected(false);
                    drawRectangle.setSelected(false);
                    shapeIdx = -1;
                    changeIndicator(indicators);
                }
            }
        }

    }







    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setMaxWidth(1600);
        primaryStage.setMaxHeight(1200);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(470);

        primaryStage.setX(200);
        primaryStage.setY(280);
        primaryStage.setWidth(STAGE_WIDTH);
        primaryStage.setHeight(STAGE_HEIGHT);
        primaryStage.setResizable(true);


        FileWriter file = null;
        BufferedWriter writer = null;
        try {
            file = new FileWriter("copy.txt");
            writer = new BufferedWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.write(
                    ""
            );
            writer.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("SketchIt");

        BorderPane root = new BorderPane();

        Group shapes = new Group();
        Group indicators = new Group();
        root.getChildren().add(shapes);

        SketchItToolPalette toolBar = new SketchItToolPalette(shapes, indicators);
        root.setLeft(toolBar);
        root.setAlignment(toolBar, Pos.CENTER);

        root.setTop(toolBar.getSIM());

        Text bot = new Text("This is a line to show dynamic resizing...");
        root.setBottom(bot);
        root.setAlignment(bot, Pos.CENTER);


        root.getChildren().add(indicators);
        Scene scene = new Scene(root, 1280, 720);

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                toolBar.mousePressedHandler(event, shapes, indicators);
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                toolBar.mouseDraggedHandler(event, indicators);
            }
        });

        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                toolBar.mouseReleasedHandler(event, indicators);
            }
        });
        scene.setOnKeyPressed(KeyEvent -> {
            if (toolBar.getSelectButton().isSelected()) {
                if (KeyEvent.getCode() == KeyCode.ESCAPE) {
                    toolBar.getSelectButton().setSelected(false);
                    shapeIdx = -1;
                    indicators.getChildren().clear();
                } else if (KeyEvent.getCode() == KeyCode.DELETE) {
                    toolBar.getSelectButton().setSelected(false);
                    toolBar.getTheList().remove(shapeIdx);
                    shapes.getChildren().remove(shapeIdx);
                    shapeIdx = -1;
                    indicators.getChildren().clear();
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
