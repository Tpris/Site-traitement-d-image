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

import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  //private final Map<Long, Image> images = new HashMap<>();

  private final Map< Long, Map<Long, Image>> images = new HashMap<>();

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
              if(images.containsKey(Long.valueOf("-1"))){
                images.get(Long.valueOf("-1")).put(img.getId(), img);
              }
              else{
                HashMap<Long, Image> tmp = new HashMap<Long, Image>();
                tmp.put(img.getId(), img);
                images.put(Long.valueOf("-1"), tmp );
              }
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
  public Optional<Image> retrieve(final long imgId, final long userId) {
    return Optional.ofNullable(images.get(userId).get(imgId));
    //return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll(final long userId) {
    return new ArrayList<Image>(images.get(userId).values());
  }

  @Override
  public void create(final Image img, final long userId) {
    if(images.containsKey(userId))
      images.get(userId).put(img.getId(),img);
   /* images.put(userId)
    images.put(img.getId(), img);*/
  }

  @Override
  public void update(final Image img, final String[] params, final long userId) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));
    images.get(userId).put(img.getId(), img);
   // images.put(img.getId(), img);
  }

  @Override
  public void delete(final Image img, final long userId) {
    long idRemoved = img.getId();
    images.get(userId).remove(idRemoved);

    /*  Image.updateCount(-1);
      for (Image image : images.values()) {
        if (image.getId() > idRemoved) {
          image.setId(image.getId() - 1);
        }
      }
      
      long limit = images.size();
      for (long i = idRemoved + 1; i <= limit; i++) {
        images.put(images.get(i).getId(), images.get(i));
        images.remove(i);
      }*/
     

  }

  @Override
  public List<Image> retrieveList(long idUser, long idStart, int size) {
    List<Image> imgs = new ArrayList<>();
    Map<Long, Image> tmp = images.get(idUser);


    long cpt = 0;
    long index = idStart;
    while (cpt != size && index <= Image.getCount()) {
      if (tmp.containsKey(index)) {
        imgs.add(tmp.get(index));
        cpt++;
      }
      index++;
    }

    return imgs;
  }

  public int getNumberImages(final long userId) {
    return images.get(userId).size();
  }

  public boolean userListExists(long userId) {
    return images.containsKey(userId);
  }


}
