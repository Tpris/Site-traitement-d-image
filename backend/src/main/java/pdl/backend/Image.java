package pdl.backend;

import org.springframework.http.MediaType;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;
  private MediaType type;
  private int size;

  public Image(final String name, final byte[] data, final MediaType type) {
    id = count++;
    this.name = name;
    this.data = data;
    this.type = type;
    this.size = data.length;
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

  public int getSize() {
    return size;
  }

}
