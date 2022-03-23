package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.ConvertRaster;
import boofcv.io.image.UtilImageIO;
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

  private static int lenValue(HashMap<String,ArrayList<String>> listParam, String param){
    return listParam.get(param).size();
  }

  private static String getAndPopValue(HashMap<String,ArrayList<String>> listParam, String param){
    String val = listParam.get(param).get(0);
    listParam.get(param).remove(0);
    return val;
  }

  private static ResponseEntity<?> imgProcessing(Planar<GrayU8> img, String algo, HashMap<String,ArrayList<String>> listParam) {
    switch (algo) {
      case "filter":
        if (lenValue(listParam,"hue")>0 && lenValue(listParam,"smin")>0 && lenValue(listParam,"smax")>0) {
          return ImageProcessing.filter(img, Float.parseFloat(getAndPopValue(listParam,"hue")), 
            Float.parseFloat(getAndPopValue(listParam,"smin")), Float.parseFloat(getAndPopValue(listParam,"smax")));  
        }
        return new ResponseEntity<>("missing parameter", HttpStatus.BAD_REQUEST);
      case "gaussianBlur":
        if (lenValue(listParam,"size")>0 && lenValue(listParam,"sigma")>0 && lenValue(listParam,"BT")>0) {
          return ImageProcessing.gaussianBlur(img, Integer.parseInt(getAndPopValue(listParam,"size")), 
              Integer.parseInt(getAndPopValue(listParam,"sigma")), stringToBorderType(getAndPopValue(listParam,"BT")));
        }
        return new ResponseEntity<>("missing parameter", HttpStatus.BAD_REQUEST);
      case "meanBlur":
        if (lenValue(listParam,"size")>0 && lenValue(listParam,"BT")>0) {
          return ImageProcessing.meanFilterWithBorders(img, Integer.parseInt(getAndPopValue(listParam,"size")), 
              stringToBorderType(getAndPopValue(listParam,"BT")));
        }
        return new ResponseEntity<>("missing parameter", HttpStatus.BAD_REQUEST);
      case "luminosity":
        if (lenValue(listParam,"delta")>0) {
          return ImageProcessing.luminosityImage(img, Integer.parseInt(getAndPopValue(listParam,"delta")));
        } 
        return new ResponseEntity<>("missing parameter", HttpStatus.BAD_REQUEST);
      case "sobel":
        return ImageProcessing.sobelImage(img, false);
      case "sobelColor":
        return ImageProcessing.sobelImage(img, true);
      case "egalisationV":
        return ImageProcessing.egalisationV(img);
      case "egalisationS":
        return ImageProcessing.egalisationS(img);
      default :
        return new ResponseEntity<>("The algorithm "+algo+" doesn't exist", HttpStatus.BAD_REQUEST);
    }
  }

  private static BorderType stringToBorderType(String BT) {
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

  private ArrayList<String> createList(Optional<String> requestParam, String separator){
    return (requestParam.isPresent())? new ArrayList<String>(Arrays.asList(requestParam.get().split(separator))):new ArrayList<String>();
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
  public @ResponseBody ResponseEntity<?> getImageProcessing(@PathVariable("id") long id,
      @RequestParam("algorithm") Optional<String> algo,
      @RequestParam("delta") Optional<String> delta,
      @RequestParam("size") Optional<String> size,
      @RequestParam("sigma") Optional<String> sigma,
      @RequestParam("BT") Optional<String> BT,
      @RequestParam("hue") Optional<String> hue,
      @RequestParam("smin") Optional<String> smin,
      @RequestParam("smax") Optional<String> smax) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      InputStream inputStream = new ByteArrayInputStream(image.get().getData());
      if (algo.isPresent()) {
        String separator = "_";
        ArrayList<String> algos = new ArrayList<String>(Arrays.asList(algo.get().split(separator)));
        // ArrayList<String> algos = createList(algo, separator);
        HashMap<String, ArrayList<String>> listParam = new HashMap<String, ArrayList<String>>(){{ 
          put("delta", createList(delta, separator));
          put("size", createList(size, separator));
          put("sigma", createList(sigma, separator));
          put("BT", createList(BT, separator));
          put("hue", createList(hue, separator));
          put("smin", createList(smin, separator));
          put("smax", createList(smax, separator));
        }};
        try {
          BufferedImage imBuff = ImageIO.read(inputStream);
          Planar<GrayU8> img = ConvertBufferedImage.convertFromPlanar(imBuff, null, true, GrayU8.class);

          // ConvertRaster.orderBandsIntoRGB(img, imBuff);

          System.out.println(img.getNumBands());
          if (img.getNumBands()==4){
            GrayU8[] bands = new GrayU8[3];
            for (int i = 0; i < 3; ++i)
              bands[i] = img.getBand(i).clone();
            img.setBands(bands);
          }
          System.out.println(" can2 = "+img.getNumBands());

          for(String alg : algos){
            ResponseEntity<?> res = imgProcessing(img, alg, listParam);
            if(res.getStatusCode()==HttpStatus.BAD_REQUEST){
              return res;
            }
          }
          for(ArrayList<String> param : listParam.values()){
            if(!param.isEmpty()) return new ResponseEntity<>("parameter not used", HttpStatus.BAD_REQUEST);
          }

          String outputPath = "/home/naby/Images/test4.jpg";
          UtilImageIO.saveImage(img, outputPath);

          ConvertRaster.planarToBuffered_U8(img, imBuff);
          ByteArrayOutputStream os = new ByteArrayOutputStream();
          ImageIO.write(imBuff, "png", os);
          inputStream = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
          return new ResponseEntity<>("Image id=" + id + " can't be treated", HttpStatus.BAD_REQUEST);
        }
      }
      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(inputStream));
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
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString()) && !contentType.equals(MediaType.IMAGE_PNG.toString())) {
      return new ResponseEntity<>("Only JPEG and PNG file format supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    try {
      imageDao.create(new Image(file.getOriginalFilename(), file.getBytes()));
    } catch (IOException e) {
      return new ResponseEntity<>("Failure to read file", HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(e, HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>("Image uploaded", HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList(
      @RequestParam("index") Optional<Long> index,
      @RequestParam("size") Optional<Integer> size) {

    List<Image> images = new ArrayList<>();
    if (index.isPresent() && size.isPresent()) {
      images = imageDao.retrieveGroup(index.get(), size.get());
    } else {
      images = imageDao.retrieveAll();
    }

    ArrayNode nodes = mapper.createArrayNode();
    ObjectNode numberNode = mapper.createObjectNode();
    numberNode.put("nbImages", imageDao.getNumberImages());
    nodes.add(numberNode);
    for (Image image : images) {
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());
      objectNode.put("name", image.getName());
      objectNode.put("type", image.getType().toString());
      objectNode.put("size", image.getSize());
      nodes.add(objectNode);
    }
    return nodes;
  }

}
