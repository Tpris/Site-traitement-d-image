package pdl.backend;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws IOException {

    System.out.println("-------------------------------------------");
    System.out.println("-------------------------------------------");
    System.out.println("-------------------------------------------");

    ClassLoader classLoader = getClass().getClassLoader();
    File directory = new File(classLoader.getResource("images").getFile());

    if (!directory.exists()) {
      Path path = Paths.get(directory.getPath());
      Files.createDirectories(path);
      directory = new File(classLoader.getResource("images").getFile());
    }

    byte[] fileContent;
    try {
      File[] files = directory.listFiles();

      for (File file : files) {
        if (file.isFile()) {
          // System.out.println(file.getName());
          fileContent = Files.readAllBytes(file.toPath());
          if (isImage(file)) {
            Image img = new Image(file.getName(), fileContent);
            images.put(img.getId(), img);
          } else {
            System.out.println(file.getName() + " is not img");
          }
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private boolean isImage(File file) {
    String fileName = file.toString();

    int index = fileName.lastIndexOf('.');
    String extension = "";
    if (index > 0) {
      extension = fileName.substring(index + 1);
    }
    return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));

    images.put(img.getId(), img);
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
  }
}
