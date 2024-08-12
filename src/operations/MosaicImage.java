package operations;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import model.ImageModel;

/**
 * Represents a mosaic command.
 */
public class MosaicImage implements ImageCommand {

  private ImageModel img;
  private int value;
  private Random rx;
  private Random ry;

  /**
   * The constructor for Mosaic Image, initializes the img and value fields.
   *
   * @param model The ImageModel that the operation will be applied to.
   * @param value The amount of seeds to use in the operation.
   * @param rx    represents the random object for the width.
   * @param ry    represents the random object for the height.
   */
  public MosaicImage(ImageModel model, int value, Random rx, Random ry) {
    Objects.requireNonNull(model);
    this.img = model;
    this.value = value;
    this.rx = rx;
    this.ry = ry;
  }

  @Override
  public ImageModel command() throws IOException {
    return img.mosaic(value, rx, ry);
  }
}
