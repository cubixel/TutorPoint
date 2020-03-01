package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sql.MySQL;


public class SubjectRequestServiceTest {

  private DataInputStream dis;
  private DataOutputStream dos;
  private int id = 1;
  private String subjectNameTest = "testName";
  private String thumbnailPathTest = "testPath";
  private String nameOfThumbnailFileTest = "testFileName";

  @Mock
  MySQL mySqlMock;

  @Mock
  ResultSet resultSet;

  private SubjectRequestService subjectRequestService;

  /**
   * This is setting up the mocked Objects, and the stubbed methods that will be called
   * by the SubjectRequestService (Unit Under Test, UUT). It also connects a DataInputStream
   * and DataOutputStream together so that the response from the SubjectRequestService
   * can be verified.
   *
   * @throws IOException PipedInput and DIS will always exist, can ignore.
   * @throws SQLException Mocked SQL, can ignore.
   */
  @BeforeEach
  public void setUp() throws IOException, SQLException {
    initMocks(this);
    when(mySqlMock.getNextSubjects(0)).thenReturn(resultSet);
    when(resultSet.getInt("id")).thenReturn(id);
    when(resultSet.getString("subjectname")).thenReturn(subjectNameTest);
    when(resultSet.getString("thumbnailpath")).thenReturn(thumbnailPathTest);
    when(resultSet.getString("filename")).thenReturn(nameOfThumbnailFileTest);

    /*
     * Creating a PipedInputStream to connect the DataOutputStream and DataInputStream together
     * this is used to read the response that the UUT writes to its DataOutputStream.
     */
    PipedInputStream pipeInput = new PipedInputStream();

    dis = new DataInputStream(pipeInput);

    dos = new DataOutputStream(new PipedOutputStream(pipeInput));

    subjectRequestService = new SubjectRequestService(dos, mySqlMock);
  }

  @AfterEach
  public void cleanUp() throws IOException {
    dis.close();
    dos.close();
  }

  @Test
  public void subjectReturnTest() throws IOException {
    subjectRequestService.getSubject();

    String subjectRequestResponse = null;

    // Reading the response of the UUT.
    while (dis.available() > 0) {
      subjectRequestResponse = dis.readUTF();
    }

    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(subjectRequestResponse, JsonObject.class);
      String action = jsonObject.get("Class").getAsString();

      if (action.equals("Subject")) {
        assertEquals(id, jsonObject.get("id").getAsInt());
        assertEquals(subjectNameTest, jsonObject.get("name").getAsString());
        assertEquals(nameOfThumbnailFileTest, jsonObject.get("nameOfThumbnailFile").getAsString());
        assertEquals(thumbnailPathTest, jsonObject.get("thumbnailPath").getAsString());
      } else {
        fail("Returned a json string but it didn't contain a Subject Class.");
      }
    } catch (JsonSyntaxException jse) {
      fail("Didn't return a json string.");
    }

    assertEquals(1, subjectRequestService.getNumberOfSubjectsSent());
  }

}
