package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.ConvertRaster;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import pdl.imageprocessing.ImageProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class ImageController<Item> {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  // @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces
  // = MediaType.IMAGE_JPEG_VALUE)
  // public ResponseEntity<?> getImage(@PathVariable("id") long id) {

  // Optional<Image> image = imageDao.retrieve(id);

  // if (image.isPresent()) {
  // InputStream inputStream = new ByteArrayInputStream(image.get().getData());
  // return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new
  // InputStreamResource(inputStream));
  // }
  // return new ResponseEntity<>("Image id=" + id + " not found.",
  // HttpStatus.NOT_FOUND);
  // }

  private Planar<GrayU8> imgProcessing(Planar<GrayU8> img, String algo,
      Optional<Double> p1, Optional<Double> p2, Optional<Double> p3, Optional<String> BT) {
    switch (algo) {
      case "filter":
        if (p1.isPresent() && p2.isPresent() && p3.isPresent()) {
          ImageProcessing.filter(img, p1.get(), p2.get(), p3.get());
        }
        break;
      case "gaussianBlur":
        if (p1.isPresent() && p2.isPresent() && BT.isPresent()) {
          ImageProcessing.flouGaussien(img, p1.get(), p2.get(), stringToBorderType(BT.get()));
        }
        break;
      case "meanBlur":
        if (p1.isPresent() && BT.isPresent()) {
          ImageProcessing.meanFilterWithBorders(img, p1.get(), stringToBorderType(BT.get()));
        }
        break;
      case "luminosity":
        if (p1.isPresent()) {
          ImageProcessing.luminositeImage(img, p1.get());
        }
        break;
      case "sobel":
        ImageProcessing.contoursImage(img);
        break;
      case "egalisationV":
        ImageProcessing.egalisationS(img);
        break;
      case "egalisationS":
        ImageProcessing.egalisationV(img);
        break;
    }
    return img;
  }

  private BorderType stringToBorderType(String BT) {
    switch (BT) {
      case "NORMALIZED":
        return BorderType.NORMALIZED;
      case "EXTENDED":
        return BorderType.EXTENDED;
      case "REFLECT":
        return BorderType.REFLECT;
      default:
        return BorderType.SKIP;
    }
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public @ResponseBody ResponseEntity<?> getImageProcessing(@PathVariable("id") long id,
      @RequestParam("algorithm") Optional<String> algo,
      @RequestParam("p1") Optional<Double> p1,
      @RequestParam("p2") Optional<Double> p2,
      @RequestParam("p3") Optional<Double> p3,
      @RequestParam("BT") Optional<String> BT) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      InputStream inputStream = new ByteArrayInputStream(image.get().getData());
      if (algo.isPresent()) {
        try {
          BufferedImage imBuff = ImageIO.read(inputStream);
          Planar<GrayU8> img = ConvertBufferedImage.convertFromPlanar(imBuff, null, true, GrayU8.class);

          img = imgProcessing(img, algo.get(), p1, p2, p3, BT);

          ConvertRaster.planarToBuffered_U8(img, imBuff);
          ByteArrayOutputStream os = new ByteArrayOutputStream();
          ImageIO.write(imBuff, "jpeg", os);
          inputStream = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          System.out.println("tototoCatch");
          return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
        }
      }
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      imageDao.delete(image.get());
      return new ResponseEntity<>("Image id=" + id + " deleted.", HttpStatus.OK);
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    String contentType = file.getContentType();
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString())) {
      return new ResponseEntity<>("Only JPEG file format supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    try {
      imageDao.create(new Image(file.getOriginalFilename(), file.getBytes()));
    } catch (IOException e) {
      return new ResponseEntity<>("Failure to read file", HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>("Image uploaded", HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() {
    List<Image> images = imageDao.retrieveAll();
    ArrayNode nodes = mapper.createArrayNode();
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());
      objectNode.put("name", image.getName());
      nodes.add(objectNode);
    }
    return nodes;
  }

}
