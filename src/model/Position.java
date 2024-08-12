package model;

/**
 * Represents a 2D position.
 */
public class Position {
  private final int x;
  private final int y;

  /**
   * Deafult constructor.
   *
   * @param y represents y-axis value.
   * @param x represents x-axis value.
   */
  public Position(int y, int x) {
    this.y = y;
    this.x = x;
  }

  /**
   * Returns the x-component of the position.
   *
   * @return <code>this.x</code>.
   */
  public int getX() {
    return this.x;
  }

  /**
   * Returns the y-component of the position.
   *
   * @return <code>this.y</code>.
   */
  public int getY() {
    return this.y;
  }

  /**
   * Determines whether two positions are the same.
   *
   * @param o represents the object to be compared.
   * @return true or false based on whether this and the given object are the same.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Position)) {
      return false;
    }

    Position other = (Position) o;

    return other.x == this.x && other.y == this.y;
  }

  /**
   * Creates a hashCode for a Position.
   *
   * @return the hashcode of the position.
   */
  @Override
  public int hashCode() {
    return Integer.hashCode(this.x) + Integer.hashCode(this.y);
  }
}
