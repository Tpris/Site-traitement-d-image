package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.File;
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

  public Image(final File file, MediaType type) throws IOException {
    id = count++;
    this.name = file.getName();
    this.data = Files.readAllBytes(file.toPath());
    this.type = type;
    BufferedImage img = ImageIO.read(file);
    this.size = createSize(img);
  }

  private String createSize(BufferedImage img) {
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

}
