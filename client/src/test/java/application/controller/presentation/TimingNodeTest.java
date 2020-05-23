package application.controller.presentation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TimingNodeTest {
  @Test
  public void testConstruction() {
    TimingNode node = new TimingNode("0:0", -1, "type");
  }

  @Test
  public void testDataRetrieval() {
    TimingNode node = new TimingNode("0:0", -1, "type");
    assertTrue(node.getId().equals("0:0"));
    assertTrue(node.getTime() == -1);
    assertTrue(node.getType().equals("type"));
  }

  @Test
  public void testDataStorageRetrieval() {
    TimingNode node = new TimingNode("0:0", -1, "type");
    node.setId("0:1");
    node.setTime(15);
    node.setType("otherType");
    assertTrue(node.getId().equals("0:1"));
    assertTrue(node.getTime() == 15);
    assertTrue(node.getType().equals("otherType"));
  }
}