import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;


public class SeamCarver {
  private Picture picture;

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    if (picture == null) {
      throw new IllegalArgumentException("Argument is null");
    }
    this.picture = new Picture(picture);
  }

  // current picture
  public Picture picture() {
    return picture;
  }

  // width of current picture
  public int width() {
    return picture.width();
  }

  // height of current picture
  public int height() {
    return picture.height();
  }

  // energy of pixel at column x and row y
  public double energy(int x, int y) {
    if (x > picture.width() - 1 || x < 0 || y > picture.height() - 1 || y < 0) {
      throw new IllegalArgumentException("X or Y out of bounds");
    }

    if (x == 0 || x == picture.width() - 1 || y == 0 || y == picture.height() - 1) {
      return 1000;
    } else {
      Color xMinus = picture.get(x - 1, y);
      Color xPlus = picture.get(x + 1, y);
      Color yMinus = picture.get(x, y - 1);
      Color yPlus = picture.get(x, y + 1);
      double xGradient = Math.pow(xMinus.getRed() - xPlus.getRed(), 2) +
          Math.pow(xMinus.getGreen() - xPlus.getGreen(), 2) +
          Math.pow(xMinus.getBlue() - xPlus.getBlue(), 2);
      double yGradient = Math.pow(yMinus.getRed() - yPlus.getRed(), 2) +
          Math.pow(yMinus.getGreen() - yPlus.getGreen(), 2) +
          Math.pow(yMinus.getBlue() - yPlus.getBlue(), 2);
      double gradient = Math.sqrt(xGradient + yGradient);
      return gradient;
    }
  }

  // sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    int[] seam = new int[width()];
    for (int x = 0; x < width(); x++) {
      double minEnergy = Double.POSITIVE_INFINITY;
      int yMin = 0;
      for (int y = 0; y < height(); y++) {
        if (x == 0 || x == 1) {
          if (energy(x, y) < minEnergy) {
            minEnergy = energy(x, y);
            yMin = y;
          }
        } else if (y + 1 == seam[x - 1] || y == seam[x - 1] || y - 1 == seam[x - 1]) {
          if (energy(x, y) < minEnergy) {
            minEnergy = energy(x, y);
            yMin = y;
          }
        }
      }
      seam[x] = yMin;
    }
    seam[0] = seam[1];
    return seam;
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    int[] seam = new int[height()];
    for (int y = 0; y < height(); y++) {
      double minEnergy = Double.POSITIVE_INFINITY;
      int xMin = 0;
      for (int x = 0; x < width(); x++) {
        if (y == 0 || y == 1) {
          if (energy(x, y) < minEnergy) {
            minEnergy = energy(x, y);
            xMin = x;
          }
        } else if (x + 1 == seam[y - 1] || x == seam[y - 1] || x - 1 == seam[y - 1]) {
          if (energy(x, y) < minEnergy) {
            minEnergy = energy(x, y);
            xMin = x;
          }
        }
      }
      seam[y] = xMin;
    }
    seam[0] = seam[1];
    return seam;
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Argument is null");
    } else if (seam.length != width()) {
      throw new IllegalArgumentException("Array incorrect size");
    }

    Picture newPicture = new Picture(width(), height() - 1);
    for (int x = 0; x < newPicture.width(); x++) {
      for (int y = 0; y < newPicture.height(); y++) {
        if (y < seam[x]) {
          newPicture.setRGB(x, y, picture.getRGB(x, y));
        } else {
          newPicture.setRGB(x, y, picture.getRGB(x, y + 1));
        }
      }
    }

    picture = newPicture;
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Argument is null");
    } else if (seam.length != height()) {
      throw new IllegalArgumentException("Array incorrect size");
    }

    Picture newPicture = new Picture(width() - 1, height());
    for (int y = 0; y < newPicture.height(); y++) {
      for (int x = 0; x < newPicture.width(); x++) {
        if (x < seam[y]) {
          newPicture.setRGB(x, y, picture.getRGB(x, y));
        } else {
          newPicture.setRGB(x, y, picture.getRGB(x + 1, y));
        }
      }
    }

    picture = newPicture;
  }

  //  unit testing (optional)
  public static void main(String[] args) {
    //Testing
  }

}