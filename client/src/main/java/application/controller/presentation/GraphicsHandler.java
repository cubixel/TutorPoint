package application.controller.presentation;

import java.util.ArrayList;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class GraphicsHandler {
  private StackPane pane;
  private ArrayList<Shape> shapes = new ArrayList<Shape>();// shape objects
  private ArrayList<String> ids = new ArrayList<String>();//corresponding IDs
  private int paneWidth;
  private int paneHeight;

  /**
   * constructor.
   * @param targetPane the stackpane to draw objects to.
   */
  public GraphicsHandler(StackPane targetPane) {
    this.pane = targetPane;
    paneWidth = (int)pane.getMinWidth();
    paneHeight = (int)pane.getMinHeight();
  }

  /**
   * Registers a solid line using the provided parameters.
   * 
   * @param xstart the x coordinate of the start of the line
   * @param xend the x coordinate of the end of the line
   * @param ystart the y coordinate of the start of the line
   * @param yend the y coordinate of the end of the line
   * @param lineColour the line color
   * @param id the shape id.
   */
  public void registerLine(float xstart, float xend, float ystart, float yend, String lineColour, 
      String id) {
    Color lc = Color.web(lineColour);//creates colour from hash code
    int lineWidth = 5;//line width
    Shape shape = new Shape(lc,lc,paneWidth,paneHeight,lineWidth);//creates shape object
    //adds start point to object
    shape.addPoint((int)(paneWidth * xstart), (int) (paneHeight * ystart));
    //adds end point to object
    shape.addPoint((int)(paneWidth * xend), (int) (paneHeight * yend));
    shapes.add(shape);//adds shape to array
    shape.create();//constructs the shape group
    addId(id);//adds either unique id to array or "invalid id assigned"
  }

  /**
   * Registers a solid rectangle using the provided parameters.
   * 
   * @param xstart the x coordinate of the top left of the rectangle
   * @param ystart the y coordinate of the top left of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param fillColour the fill color
   * @param id the shape id
   */
  public void registerRectangle(float xstart, float ystart, float width, float height, 
      String fillColour, String id) {
    Color fc = Color.web(fillColour);
    Shape shape = new Shape(fc, fc, paneWidth, paneHeight, 0);
    //adds the 4 points to the object
    shape.addPoint((int) (paneWidth * xstart), (int) (paneHeight * ystart));
    shape.addPoint((int) (paneWidth * (xstart + width)), (int) (paneHeight * ystart));
    shape.addPoint((int) (paneWidth * (xstart + width)), (int) (paneHeight * (ystart + height)));
    shape.addPoint((int) (paneWidth * xstart), (int) (paneHeight * (ystart + height)));
    
    shapes.add(shape);
    shape.create();
    addId(id);
    
  }

  /**
   * Registers a gradient shaded rectangle using the provided parameters.
   * 
   * @param xstart the x coordinate of the top left of the rectangle
   * @param ystart the y coordinate of the top left of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param id the shape id
   * @param shadingX1 shading x1 coordinate
   * @param shadingY1 shading y1 coordinate
   * @param shadingColor1 shading color 1
   * @param shadingX2 shading x2 coordinate
   * @param shadingY2 shading y2 coordinate
   * @param shadingColor2 shading color 2
   * @param cyclic cyclic shading
   */
  public void registerRectangle(float xstart, float ystart, float width, float height, String id, 
      float shadingX1, float shadingY1, String shadingColor1, float shadingX2, 
      float shadingY2, String shadingColor2, Boolean cyclic) {
    Color c1 = Color.web(shadingColor1);//creates first colour of gradient
    Color c2 = Color.web(shadingColor2);//creates the second colour of the gradient
    Shape shape = new Shape(paneWidth,paneHeight, 0, c1, c2, (int) (paneWidth * shadingX1), 
        (int) (paneHeight * shadingY1), (int) (paneWidth * shadingX2), 
        (int) (paneHeight * shadingY2), cyclic); //shape with gradient fill
    //adds the 4 points to the object
    shape.addPoint((int) (paneWidth * xstart), (int) (paneHeight * ystart));
    shape.addPoint((int) (paneWidth * (xstart + width)),(int) (paneHeight * ystart));
    shape.addPoint((int) (paneWidth * (xstart + width)),(int) (paneHeight * (ystart + height)));
    shape.addPoint((int) (paneWidth * xstart), (int) (paneHeight * (ystart + height)));
    
    shapes.add(shape);
    shape.create();
    addId(id);

  }

  /**
   * Registers a solid oval using the provided parameters.
   * 
   * @param xstart the x coordinate of the top left of the oval
   * @param ystart the y coordinate of the top left of the oval
   * @param width the width of the oval
   * @param height the height of the oval
   * @param fillColour the fill color
   * @param id the shape id
   */
  public void registerOval(float xstart, float ystart, float width, float height, 
      String fillColour, String id) {
    Color fc = Color.web(fillColour);
    Shape shape = new Shape(fc,fc,paneWidth,paneHeight,0);
    shape.drawOval((int) (paneWidth * width), (int) (paneHeight * height), 
        (int) (paneWidth * xstart), (int) (paneHeight * ystart));//creates oval
    shapes.add(shape);
    shape.create();
    addId(id);
  }

  /**
   * Registers a gradient shaded oval using the provided parameters.
   * 
   * @param xstart the x coordinate of the top left of the oval
   * @param ystart the y coordinate of the top left of the oval
   * @param width the width of the oval
   * @param height the height of the oval
   * @param id the shape id
   * @param shadingX1 shading x1 coordinate
   * @param shadingY1 shading y1 coordinate
   * @param shadingColor1 shading color 1
   * @param shadingX2 shading x2 coordinate
   * @param shadingY2 shading y2 coordinate
   * @param shadingColor2 shading color 2
   * @param cyclic cyclic shading
   */
  public void registerOval(float xstart, float ystart, float width, float height, String id, 
      float shadingX1, float shadingY1, String shadingColor1, float shadingX2, float shadingY2, 
      String shadingColor2, Boolean cyclic) {
    Color c1 = Color.web(shadingColor1);
    Color c2 = Color.web(shadingColor2);
    Shape shape = new Shape(paneWidth, paneHeight, 0, c1, c2, (int) (paneWidth * shadingX1), 
        (int) (paneHeight * shadingY1), (int) (paneWidth * shadingX2), 
        (int) (paneHeight * shadingY2), cyclic);
    shape.drawOval((int) (paneWidth * width), (int) (paneHeight * height), 
        (int) (paneWidth * xstart), (int) (paneHeight * ystart));
    shapes.add(shape);
    shape.create();
    addId(id);
    
  }

  /**
   * Undraws a shape from the StackPane by id.
   * 
   * @param id the id of the shape to be undrawn
   */
  public void undrawGraphic(String id) {
    if (id.equals("invalid id assigned")) {
      return;
    }
    int i = ids.indexOf(id);// finds index in id arrayList
    if (i >= 0) {
      if (pane.getChildren().contains(shapes.get(i).get())) { //checks if shape is drawn
        pane.getChildren().remove(shapes.get(i).get());//removes if drawn
      }
    }
  }
  
  /**
   * Draw the shape with the corresponding id to the StackPane.
   * 
   * @param id the id of the shape to be drawn.
   */
  public void drawGraphic(String id) {
    if (id.equals("invalid id assigned")) {
      return;
    }
    int i = ids.indexOf(id);//finds index of id in id arrayList
    if (i >= 0) {
      //checks if shape is already drawn
      if (pane.getChildren().contains(shapes.get(i).get()) == false) {
        pane.getChildren().add(shapes.get(i).get());//draws the shape
      }
    }
  }
  
  private void addId(String id) { //used to validate the ID
    int length = ids.size();
    Boolean unique = true;
    for (int i = 0;i < length;i++) { //checks id against all others in array list
      if (ids.get(i).equals(id)) {
        unique = false;
        break;
      }
    }
    if (unique == true) { //if unique original id is added to the arrayList
      ids.add(id);
    } else if (unique == false) { //if not unique then id is replaced with "invalid id assigned"
      ids.add("invalid id assigned");
    }
  }
}
