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

import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws Exception {

    ClassLoader classLoader = getClass().getClassLoader();

    try {
      File directory = new File(classLoader.getResource("images").getFile());
      ArrayList<File> directories = new ArrayList<>();
      directories.add(directory);

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
      throw new ImagesDirectoryException("Error: Images directory doesn't exist !");
    } catch (final Exception e) {
      throw new ImagesDirectoryException("Error: Images directory doesn't exist !");
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

    Image[] imgs = images.values().toArray(new Image[0]);
    for(long i = idStart; i < idStart + size && i < Image.getCount(); i++){
      imagesList.add(imgs[(int)i]);
    }
    return imagesList;
  }

  @Override
  public List<Image> retrieveWithFilters(final String type, final String nameImg) {
    ArrayList<Image> imagesList = new ArrayList<>();

    for(Image img: images.values()){
      if(img.getName().startsWith(nameImg) && ( type.equals("all") || img.getType().equals(MediaType.parseMediaType("image/"+type))  )){
        imagesList.add(img);
      }
    }

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

      Image.updateCount(-1);
      for (Image image : images.values()) {
        if (image.getId() > idRemoved) {
          image.setId(image.getId() - 1);
        }
      }
      
      long limit = images.size();
      for (long i = idRemoved + 1; i <= limit; i++) {
        images.put(images.get(i).getId(), images.get(i));
        images.remove(i);
      }
     

  }

  public int getNumberImages() {
    return images.size();
  }

}
