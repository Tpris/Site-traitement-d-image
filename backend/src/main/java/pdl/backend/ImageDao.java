package pdl.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws Exception {

    ClassLoader classLoader = getClass().getClassLoader();
    File directory = new File(classLoader.getResource("images").getFile());

    if (!directory.exists()) {
      Path path = Paths.get(directory.getPath());
      Files.createDirectories(path);
      directory = new File(classLoader.getResource("images").getFile());
    }

    ArrayList<File> directories = new ArrayList<>();
    directories.add(directory);

    try {
      while (directories.size() != 0) {
        File currentDirectory = directories.get(0);
        File[] files = currentDirectory.listFiles();

        for (File file : files) {
          byte[] fileContent;
          if (file.isDirectory()) {
            directories.add(file);
          } else if (file.isFile()) {
            if (isImage(file)) {
              fileContent = Files.readAllBytes(file.toPath());
              Image img = new Image(file.getName(), fileContent);
              images.put(img.getId(), img);
            }
          }
        }

        directories.remove(currentDirectory);
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
  public List<Image> retrieveGroup(final long idStart, final int size) {
    ArrayList<Image> imagesList = new ArrayList<>();

    long cpt = 0;
    long index = idStart;
    while (cpt != size && index <= Image.getCount()) {
      if (images.containsKey(index)) {
        imagesList.add(images.get(index));
        cpt++;
      }
      index++;
    }

    /*
     * for (long index = idStart; index < idStart + size; index++) {
     * imagesList.add(images.get(index));
     * }
     */
    return imagesList;
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
    long idRemoved = img.getId();
    images.remove(idRemoved);

    // POUR REINIT LE COUNT ID DES IMAGES
    /*
     * Image.updateCount(-1);
     * for (Image image : images.values()) {
     * if (image.getId() > idRemoved) {
     * image.setId(image.getId() - 1);
     * }
     * }
     * 
     * long limit = images.size();
     * for (long i = idRemoved + 1; i <= limit; i++) {
     * images.put(images.get(i).getId(), images.get(i));
     * images.remove(i);
     * }
     */

  }

  public int getNumberImages() {
    return images.size();
  }

}
