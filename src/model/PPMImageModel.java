package model;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import operations.ImageCommand;

/**
 * This class will be a model for the image, a data representation of a ppm file
 * that has a list of integers representing the pixel values, file name, etc.
 */
public class PPMImageModel implements ImageModel {

  private final String fileName;
  private final String givenName;

  private int width;

  private int height;

  private int maxValue;

  private List<Integer> pixelList;

  /**
   * This is the default constructor used when images are first created / loaded in.
   *
   * @param fileName  A String, the path of the image on the computer.
   * @param givenName A String, the name that is assigned to the image while it is in the program.
   */
  public PPMImageModel(String fileName, String givenName) {
    this.fileName = fileName;
    this.givenName = givenName;
    createImage();
  }

  /**
   * This is the constructor for when a new image is needed to be made from an old one, transferring
   * over all of its fields.
   *
   * @param fileName  A String, the path of the image on the computer.
   * @param givenName A String, the name that is assigned to the image while it is in the program.
   * @param height    An int, the height of the image.
   * @param width     An int, the width of the image.
   * @param maxValue  An int, the maximum value of a RGB value in the image.
   * @param pixelList A List of Integers, the list of all the pixel values.
   */
  public PPMImageModel(String fileName, String givenName, int height, int width,
                       int maxValue, List<Integer> pixelList) {
    this.fileName = fileName;
    this.givenName = givenName;
    this.height = height;
    this.width = width;
    this.maxValue = maxValue;
    this.pixelList = pixelList;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public List<Integer> getImage() {
    return pixelList;
  }


  /**
   * This method repurposes a lot of the ImageUtil code so that instead of printing the
   * values of the pixels of an image, the method actually stores the data in a List of Int.
   */
  private void createImage() {
    Scanner sc;

    try {
      sc = new Scanner(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("Sorry that is an invalid file, try again.");
    }

    StringBuilder builder = new StringBuilder();
    //read the file line by line, and populate a string. This will throw away any comment lines
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s + System.lineSeparator());
      }
    }

    //now set up the scanner to read from the string we just built
    sc = new Scanner(builder.toString());

    String token;

    token = sc.next();
    if (!token.equals("P3")) {
      throw new IllegalArgumentException("This is not a valid ppm");
    }
    this.width = sc.nextInt();
    this.height = sc.nextInt();
    this.maxValue = sc.nextInt();

    pixelList = new ArrayList<>();

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        pixelList.add(sc.nextInt());
        pixelList.add(sc.nextInt());
        pixelList.add(sc.nextInt());
      }
    }
  }

  @Override
  public ImageModel runCommand(ImageCommand cmd) throws IOException {
    return cmd.command();
  }

  @Override
  public ImageModel mosaic(int value, Random rx, Random ry) {
    if (value == 0 || value == this.width * this.height) {
      return this;
    }
    Coord px = new Coord(this.pixelList, this.height, this.width);
    ArrayList<ArrayList<ArrayList<Integer>>> img = px.getImage();
    ArrayList<Position> closest = new ArrayList<>();
    ArrayList<Position> pos = new ArrayList<>();
    while (pos.size() != value) {
      int randy = rx.nextInt(this.height);
      int randx = ry.nextInt(this.width);
      Position p = new Position(randy, randx);
      if (!pos.contains(p)) {
        pos.add(p);
      }
    }

    Map<Position, ArrayList<Position>> groups = new HashMap<>();
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        Position seed = null;
        double min = -1;
        for (Position p : pos) {
          double dist = getDistance(new Position(i, j), p);
          if (min == -1) {
            seed = p;
            min = dist;
          }
          if (dist <= min) {
            seed = p;
            min = dist;
          }
        }
        if (!groups.containsKey(seed)) {
          groups.put(seed, new ArrayList<>());
        }
        ArrayList<Position> updated = groups.get(seed);
        updated.add(new Position(i, j));
        groups.replace(seed, updated);
      }
    }
    ArrayList<Integer> average_red = new ArrayList<>();
    ArrayList<Integer> average_green = new ArrayList<>();
    ArrayList<Integer> average_blue = new ArrayList<>();

    int red = 0;
    int green = 0;
    int blue = 0;

    for (Map.Entry<Position, ArrayList<Position>> entry : groups.entrySet()) {
      for (Position p : entry.getValue()) {
        ArrayList<Integer> current = px.getPixel(p.getY(), p.getX());
        red += current.get(0);
        green += current.get(1);
        blue += current.get(2);
      }
      red /= entry.getValue().size();
      green /= entry.getValue().size();
      blue /= entry.getValue().size();

      average_red.add(red);
      average_green.add(green);
      average_blue.add(blue);
    }

    for (Map.Entry<Position, ArrayList<Position>> entry : groups.entrySet()) {
      red = average_red.remove(0);
      green = average_green.remove(0);
      blue = average_blue.remove(0);
      ArrayList<Integer> altered = new ArrayList<>();
      altered.add(red);
      altered.add(green);
      altered.add(blue);
      for (Position p : entry.getValue()) {
        img.get(p.getY()).set(p.getX(), altered);
      }
    }
    List<Integer> new_list = new ArrayList<Integer>();
    for (ArrayList<ArrayList<Integer>> x : img) {
      for (ArrayList<Integer> y : x) {
        new_list.addAll(y);
      }
    }
    return new PPMImageModel(this.fileName, this.givenName, this.height,
            this.width, this.maxValue, new_list);
  }


  private double getDistance(Position a, Position b) {
    return Math.sqrt((Math.pow(b.getX() - a.getX(), 2)) + (Math.pow(b.getY() - a.getY(), 2)));
  }

  @Override
  public ImageModel brighten(int value) {
    List<Integer> new_list = new ArrayList<Integer>();
    for (int i = 0; i < pixelList.size(); i++) {
      int bruh = pixelList.get(i);
      if (bruh + value >= 255) {
        new_list.add(255);
      } else if (bruh + value <= 0) {
        new_list.add(0);
      } else {
        new_list.add(bruh + value);
      }
    }
    return new PPMImageModel(this.fileName, this.givenName, this.height,
            this.width, this.maxValue, new_list);
  }


  @Override
  public ImageModel horizontalFlip() {
    List<Integer> new_list = new ArrayList<Integer>();

    TreeMap<Integer, int[]> pixel_clumps = this.groupPixels();
    List<int[]> new_clumps = new ArrayList<int[]>();
    for (int i = 0; i < this.getHeight(); i++) {
      for (int j = this.getWidth() - 1; j > -1; j--) {
        new_clumps.add(pixel_clumps.get(j + (this.getWidth() * i)));
      }
    }

    for (int[] i : new_clumps) {
      new_list.add(i[0]);
      new_list.add(i[1]);
      new_list.add(i[2]);
    }

    return new PPMImageModel(this.fileName, this.givenName, this.height,
            this.width, this.maxValue, new_list);
  }

  @Override
  public ImageModel verticalFlip() {
    List<Integer> new_list = new ArrayList<Integer>();

    TreeMap<Integer, int[]> pixel_clumps = this.groupPixels();
    List<int[]> new_clumps = new ArrayList<int[]>();
    for (int i = 1; i < this.getHeight() + 1; i++) {
      int bruh = pixel_clumps.size() - (this.getWidth() * i);
      for (int j = 0; j < this.getWidth(); j++) {
        new_clumps.add(pixel_clumps.get(bruh + j));
      }
    }

    for (int[] i : new_clumps) {
      new_list.add(i[0]);
      new_list.add(i[1]);
      new_list.add(i[2]);
    }
    return new PPMImageModel(this.fileName, this.givenName, this.height,
            this.width, this.maxValue, new_list);
  }


  @Override
  public ImageModel compGrey(String comp) {
    List<Integer> new_list = new ArrayList<Integer>();
    int grey = 0;
    for (int i = 0; i < pixelList.size(); i = i + 3) {
      int r = pixelList.get(i);
      int g = pixelList.get(i + 1);
      int b = pixelList.get(i + 2);

      switch (comp) {
        case "red":
          grey = r;
          break;
        case "blue":
          grey = b;
          break;
        case "green":
          grey = g;
          break;
        case "intensity":
          double intensity = (r + g + b) / 3;
          grey = (int) Math.round(intensity);
          break;
        case "luna":
          double luna = 0.2126 * r + 0.7152 * g + .0722 * b;
          grey = (int) Math.round(luna);
          break;
        case "value":
          grey = Math.max(Math.max(r, g), b);
          break;
        default:
          break;
      }

      new_list.add(grey);
      new_list.add(grey);
      new_list.add(grey);

    }
    return new PPMImageModel(this.fileName, this.givenName, this.height,
            this.width, this.maxValue, new_list);
  }

  private TreeMap<Integer, int[]> groupPixels() {
    TreeMap<Integer, int[]> pixel_clumps = new TreeMap<>();
    for (int i = 0; i < pixelList.size() / 3; i++) {
      int[] bruh = new int[3];
      for (int j = 0; j < 3; j++) {
        bruh[j] = pixelList.get(j + i * 3);
      }
      pixel_clumps.put(i, bruh);
    }
    return pixel_clumps;
  }


  @Override
  public BufferedImage getBuffImage() {
    return null;
  }

  @Override
  public ImageModel colorTransform(String type) throws IOException {
    ImageModel medium = this.convertBetween();
    ImageModel filtered = medium.colorTransform(type);
    return filtered.convertBetween();
  }


  @Override
  public ImageModel filterImage(String filter) throws IOException {
    ImageModel medium = this.convertBetween();
    ImageModel filtered = medium.filterImage(filter);
    return filtered.convertBetween();
  }

  @Override
  public List<Integer> makeComponentList(String component) {
    return pixelList;
  }

  @Override
  public ImageModel convertBetween() throws IOException {
    BufferedImage converted = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    int[] pixels = new int[width * height];
    for (int i = 0; i < pixelList.size(); i = i + 3) {
      int red = pixelList.get(i);
      int green = pixelList.get(i + 1);
      int blue = pixelList.get(i + 2);
      int rgb = (red << 16) | (green << 8) | (blue << 0);
      pixels[i / 3] = rgb;
    }
    converted.setRGB(0, 0, width, height, pixels, 0, width);
    return new FileModel(converted);
  }

  @Override
  public String checkType() {
    return "ppm";
  }

}
