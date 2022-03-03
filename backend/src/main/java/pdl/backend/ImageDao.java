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

    final File folder = new File(
        "C:/Users/maema/Documents/Ecole/Université/S6/Projet/Projet/Projet_pdl/backend/src/main/resources/images");

    if (folder == null || !folder.exists()) {
      Path path = Paths.get(
          "C:/Users/maema/Documents/Ecole/Université/S6/Projet/Projet/Projet_pdl/backend/src/main/resources/images");

      // java.nio.file.Files;
      Files.createDirectories(path);
    }
    byte[] fileContent;
    try {
      File[] files = folder.listFiles();

      for (File file : files) {
        if (file.isFile()) {
          System.out.println(file.getName());
          fileContent = Files.readAllBytes(file.toPath());
          Image img = new Image(file.getName(), fileContent);
          images.put(img.getId(), img);
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
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
