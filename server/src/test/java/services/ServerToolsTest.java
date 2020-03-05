package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static services.ServerTools.packageClass;
import static services.ServerTools.sendFileService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerToolsTest {

  private static DataInputStream dis;
  private static DataOutputStream dos;
  private File file;

  /**
   * Setting up TestFile and the DataInput/OutputStreams.
   */
  @BeforeEach
  public void setUp() {
    file = new File("src/test/resources/services/TestFile.txt");

    /*
     * Creating a PipedInputStream to connect the DataOutputStream and DataInputStream together
     * this is used to read the response that the UUT writes to its DataOutputStream.
     */
    try {
      PipedInputStream pipeInput = new PipedInputStream();

      dis = new DataInputStream(pipeInput);

      dos = new DataOutputStream(new PipedOutputStream(pipeInput));
    } catch (IOException e) {
      fail("Could not setup DataInput/OutputStreams.");
    }

  }

  /**
   * Close all data streams and remove the newly created file.
   */
  @AfterAll
  public static void cleanUp() {
    try {
      dis.close();
      dos.close();
    } catch (IOException e) {
      fail("Could not close DataInput/OutputStreams.");
    }
    File file = new File("src/test/resources/services/TestFileCopy.txt");

    if (file.delete()) {
      System.out.println("ServerToolsTest, Clean Up Finished.");
    } else {
      fail("Could not delete file 'TestFileCopy'.");
    }
  }

  /**
   * METHOD DESCRIPTION.
   */
  @Test
  public void packageClassTest() {
    File testObject = new File("src/test/resources/services/TestFile.txt");
    String response;

    response = packageClass(testObject);

    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
      assertEquals("File", jsonObject.get("Class").getAsString());
    } catch (JsonSyntaxException e) {
      fail("Did not return a json object.");
    }
  }

  /**
   * METHOD DESCRIPTION.
   *
   * @throws IOException DESCRIPTION
   */
  @Test
  public void sendFileServiceTest() throws IOException {
    sendFileService(dos, file);

    String fileName = dis.readUTF();
    long size = dis.readLong();

    assertEquals(file.getName(), fileName);
    int fileNumberOfBytes = 71;
    assertEquals(fileNumberOfBytes, size);

    int bytesRead;
    OutputStream output = new FileOutputStream("src/test/resources/services/TestFileCopy.txt");
    byte[] buffer = new byte[1024];
    while (size > 0
        && (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
      output.write(buffer, 0, bytesRead);
      size -= bytesRead;
    }
    output.close();

    try {
      BufferedReader bufferedReader =
          new BufferedReader(new FileReader("src/test/resources/services/TestFileCopy.txt"));
      assertEquals("This is a test file.", bufferedReader.readLine());
      bufferedReader.close();
    } catch (FileNotFoundException e) {
      fail("Copy of file not read or created from DataInputStream.");
    }
  }

}
