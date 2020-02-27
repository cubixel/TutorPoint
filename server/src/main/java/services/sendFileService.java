package services;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class sendFileService {

  public sendFileService(DataOutputStream dos, File file) {

    try {
      FileInputStream fis = new FileInputStream(file);
      byte[] buffer = new byte[4096];

      while (fis.read(buffer) > 0) {
        dos.write(buffer);
      }

      fis.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
