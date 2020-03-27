package application.controller.presentation;

/**
 * CLASS DESCRIPTION.
 *
 * @author CUBIXEL
 *
 */

public class TimingManager extends Thread {

  long slideStartTime;
  long currentTime;
  long timeElapsed;
  int slideNumber;

  public TimingManager(PresentationObject presentation) {
    setDaemon(true);
  }

  @Override
  public void run() {
    while (true) {
      currentTime = System.currentTimeMillis();


    }
  }

  public void setSlide(int number) {
    this.slideNumber = number;
    slideStartTime = System.currentTimeMillis();
  }

  public void addElement(String name, String id, String startTime, String endTime) {

  }

  public void addLoopedElement(String name, String id, String startTime, String loop) {

  }
}