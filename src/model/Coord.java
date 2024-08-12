package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 2D Cartesian Plane of pixels.
 */
public class Coord {
  ArrayList<ArrayList<ArrayList<Integer>>> pixels;
  List<Integer> org;
  int height;
  int width;


  /**
   * Default constructor.
   *
   * @param org    represents the original list of rgb values.
   * @param height represents the height of the image.
   * @param width  represents the width of the image.
   */
  public Coord(List<Integer> org, int height, int width) {
    this.org = org;
    this.height = height;
    this.width = width;
    this.pixels = this.convert(org);
  }

  /**
   * Converts a list of rgb values to a 2D array of pixels.
   *
   * @param org rerpesents the original list of rgb values.
   * @return the new 2D array list.
   */
  private ArrayList<ArrayList<ArrayList<Integer>>> convert(List<Integer> org) {
    ArrayList<ArrayList<ArrayList<Integer>>> img = new ArrayList<>();
    ArrayList<ArrayList<Integer>> width = new ArrayList<>();
    for (int i = 0; i < org.size() / 3; i++) {
      ArrayList<Integer> temp = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        temp.add(org.get(j + i * 3));
      }

      width.add(temp);

      if (width.size() == this.width) {
        img.add(width);
        width = new ArrayList<>();
      }

    }
    return img;
  }

  /**
   * Gets the 2D array list of pixels.
   *
   * @return pixels.
   */
  public ArrayList<ArrayList<ArrayList<Integer>>> getImage() {
    return pixels;
  }

  /**
   * Gets a pixel at a specific coordinate.
   *
   * @param i represents the y-position.
   * @param j represents the x-position.
   * @return the pixel at the given position.
   */
  public ArrayList<Integer> getPixel(int i, int j) {
    return this.pixels.get(i).get(j);
  }

}
