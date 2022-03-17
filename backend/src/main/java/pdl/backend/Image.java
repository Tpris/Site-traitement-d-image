package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;
  private MediaType type;
  private String size;

  public Image(final String name, final byte[] data) throws IOException {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = getType(name);
    this.size = createSize();
  }

  private String createSize() throws IOException {

    File file = new File(this.name);

    FileOutputStream fos = new FileOutputStream(file);
    fos.write(this.data);
    fos.close();

    BufferedImage img = ImageIO.read(file);

    String width = String.valueOf(img.getWidth());
    String height = String.valueOf(img.getHeight());

    return width + 'x' + height + 'x' + width.length();
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }

  public MediaType getType() {
    return type;
  }

  public String getSize() {
    return size;
  }

  private MediaType getType(String fileName) {

    int index = fileName.lastIndexOf('.');
    String extension = "";
    if (index > 0) {
      extension = fileName.substring(index + 1);
    }

    if (extension.equals("jpeg"))
      extension = "jpg";

    return extension.equals("jpg") ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG;
  }

}
