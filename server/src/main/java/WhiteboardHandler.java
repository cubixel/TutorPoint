import com.google.gson.JsonObject;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WhiteboardHandler extends Thread {

  private Canvas canvas;
  private GraphicsContext gc;
  private WritableImage snapshot;
  private String sessionID;
  private String tutorID;
  private String mouseState;
  private String previousMouseState;
  private String canvasTool;
  private boolean tutorOnlyAccess;
  private Color stroke;
  private int strokeWidth;
  private double strokeXPosition;
  private double strokeYPosition;
  private ArrayList<String> sessionUsers;

  /**
   * Constructor for WhiteboardHandler.
   * @param sessionID ID of the stream session.
   * @param tutorID ID of the tutor hosting the stream.
   */
  public WhiteboardHandler(String sessionID, String tutorID, int token) {
    setDaemon(true);
    setName("WhiteboardHandler-" + token);
    // Assign unique session ID and tutor ID to new whiteboard handler.
    this.sessionID = sessionID;
    this.tutorID = tutorID;

    // Setup the server canvas.
    this.canvas = new Canvas(1200, 790);

    // Initialise the main graphics context.
    gc = canvas.getGraphicsContext2D();
    gc.setLineCap(StrokeLineCap.ROUND);
    gc.setMiterLimit(1);

    // Set whiteboard defaults.
    this.mouseState = "idle";
    this.previousMouseState = "idle";
    this.canvasTool = "pen";
    this.tutorOnlyAccess = true;
    this.stroke = Color.BLACK;
    this.strokeWidth = 10;
    this.strokeXPosition = -1;
    this.strokeYPosition = -1;

    // Add tutor to session users.
    this.sessionUsers = new ArrayList<>();
    addUser(this.tutorID);
  }

  public void addUser(String userID) {
    this.sessionUsers.add(userID);
  }

  private void parseSessionJson(JsonObject updatePackage) {

    try {
      // Format the JSON package to a JSON object.
      JSONObject jsonObject = (JSONObject) new JSONParser().parse(updatePackage.getAsString());

      // Only allow the tutor to update the whiteboard access control.
      if (this.tutorID.equals(jsonObject.get("userID"))) {
        this.tutorOnlyAccess = (boolean) jsonObject.get("tutorOnlyAccess");
      }

      // Update the whiteboard handler's state and parameters.
      this.mouseState = (String) jsonObject.get("mouseState");
      this.canvasTool = (String) jsonObject.get("canvasTool");
      this.stroke = (Color) jsonObject.get("stroke");
      this.strokeWidth = (int) jsonObject.get("strokeWidth");
      this.strokeXPosition = (int) jsonObject.get("strokeXPosition");
      this.strokeYPosition = (int) jsonObject.get("strokeYPosition");

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * .
   * @param sessionPackage .
   */
  public void updateWhiteboard(JsonObject sessionPackage) {
    String userID = sessionPackage.get("userID").getAsString();

    // Allow tutor to update whiteboard regardless of access control.
    if (this.tutorID.equals(userID)) {
      parseSessionJson(sessionPackage);

      // TODO - Draw stroke to canvas.
      drawStroke();

    // Allow other users to update whiteboard is access control is granted.
    } else if (tutorOnlyAccess) {
      parseSessionJson(sessionPackage);

      // TODO - Draw stroke to canvas.
      drawStroke();
    }


    // Flatten new data on canvas to an image.
    this.snapshot = takeSnapshot(this.canvas);

    // Downscale and draw image to canvas' graphics context.
    canvas.getGraphicsContext2D().drawImage(snapshot, canvas.getWidth(), canvas.getWidth(),
        0, 0);
  }

  private void drawStroke() {

    // User presses mouse on canvas.
    if (previousMouseState.equals("idle") && mouseState.equals("active")) {
      gc.setStroke(this.stroke);
      gc.setLineWidth(this.strokeWidth);
      gc.beginPath();

    // User drags mouse on canvas.
    } else if (previousMouseState.equals("active") && mouseState.equals("active")) {
      gc.lineTo(this.strokeXPosition, this.strokeYPosition);
      gc.stroke();

    // User releases mouse on canvas.
    } else if (previousMouseState.equals("active") && mouseState.equals("idle")) {
      gc.closePath();
    }
  }

  private WritableImage takeSnapshot(Canvas canvas) {
    // Write a snapshot of the canvas using unscaled image to a new image.
    WritableImage image = new WritableImage((int) canvas.getWidth() * 2,
        (int) canvas.getHeight() * 2);

    return canvas.snapshot(null, image);
  }

  public ArrayList<String> getSessionUsers() {
    return this.sessionUsers;
  }

  public String getSessionID() {
    return sessionID;
  }

  public String getTutorID() {
    return tutorID;
  }

  public WritableImage getSnapshot() {
    return snapshot;
  }

  public String getCanvasTool() {
    return canvasTool;
  }
}
