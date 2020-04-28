package application.controller.presentation;


import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;

public class Shape {
  Polygon polygon = new Polygon();
  Color fill = null;
  Color line = null;
  int sideNo;
  Group group;//to be returned
  Canvas canvas; 
  GraphicsContext gc; 
  List<Double> pointsx = new ArrayList<Double>(); //points for the polygons
  List<Double> pointsy = new ArrayList<Double>();
  boolean oval = false;
  int oheight = 0;//oval height
  int owidth = 0;
  int ocx = 0;//oval reference coordinates
  int ocy = 0;
  float width;//frame size
  float height;

  /**
   * constructor.
   * 
   * @param lineColour the color of the line
   * @param fillColour the color of the fill
   * @param w the width
   * @param h the height
   * @param lw the width of the line
   */
  public Shape(Color lineColour, Color fillColour,int w, int h, int lw) {
    group = new Group();
    fill = fillColour;
    line = lineColour;
    width = w;
    height = h;
    canvas = new Canvas(width,height);
    gc = canvas.getGraphicsContext2D();
    gc.setFill(fill);
    gc.setStroke(line);
    gc.setLineWidth(lw);
  }

  /**
   * constructor for a shape with a colour gradient.
   * 
   * @param w the width
   * @param h the height
   * @param lw the width of the line
   * @param c1 c1
   * @param c2 c2
   * @param c1x c1x
   * @param c1y c1y
   * @param c2x c2x 
   * @param c2y c2y
   * @param cyclical cyclical
   */
  public Shape(int w, int h, int lw, Color c1, Color c2, float c1x, float c1y, float c2x, 
      float c2y, Boolean cyclical) {
    group = new Group();
    width = w;
    height = h;
    canvas = new Canvas(width,height);
    gc = canvas.getGraphicsContext2D();
    
    if (cyclical == true) { //sets up a cyclical gradient pattern
      Stop[] stops = new Stop[] {new Stop(0,c1), new Stop(1,c2)};
      LinearGradient lg = new LinearGradient(c1x,c1y,c2x,c2y,false,CycleMethod.REFLECT,stops);
      gc.setFill(lg);
      gc.setStroke(Color.TRANSPARENT);
    } else { //standard linear gradient
      Stop[] stops = new Stop[] {new Stop(0,c1), new Stop(1,c2)};
      LinearGradient lg = new LinearGradient(c1x,c1y,c2x,c2y,false,CycleMethod.NO_CYCLE,stops);
      gc.setFill(lg);
      gc.setStroke(Color.TRANSPARENT);
    }
  }
  
  //adds point to the polygon
  public void addPoint(double x, double y) {
    pointsx.add(x);
    pointsy.add(y);
  }
  
  /**
   * draws an oval, cx,cy correspond to distance from the top left corner to enclosing box.
   * 
   * @param width the width of the oval
   * @param height the height of the oval
   * @param cx the top left x coordinate
   * @param cy the top left y coordinate
   */
  public void drawOval(int width, int height, int cx, int cy) {
    oval = true; // changes shape from polygon to oval
    oheight = height;
    owidth = width;
    ocx = cx;
    ocy = cy;
  }
  
  /**
   * creates something.
   */
  public void create() {
    if (oval == true) {
      gc.fillOval(ocx,ocy,owidth,oheight);
      gc.strokeOval(ocx,ocy,owidth,oheight);
      group.getChildren().add(canvas);
    } else {
      // used to create an array of points from an arraylist
      double[] pointx = new double[pointsx.size()]; 
      double[] pointy = new double[pointsy.size()];
      
      //adding arraylist points to an array
      for (int i = 0; i < pointsx.size(); i++) {
        pointx[i] = pointsx.get(i);
      }
      
      for (int i = 0; i < pointsy.size(); i++) {
        pointy[i] = pointsy.get(i);
      }
      
      gc.strokePolygon(pointx,pointy,pointsx.size());//creates a polygon outline
      gc.fillPolygon(pointx,pointy,pointsx.size());//creates the polygon solid
      group.getChildren().add(canvas);//adds canvas to the group
    }
    
  }

  public Group get() {
    return group;
  }

  public void destroy() { //removes the canvas from the group
    group.getChildren().remove(canvas);
  }
}
