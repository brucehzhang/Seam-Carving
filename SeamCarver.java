import edu.princeton.cs.algs4.Picture;
import java.awt.Color;


public class SeamCarver {
  private Picture picture;
  private Picture original;

  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    if (picture == null) {
      throw new IllegalArgumentException("Argument is null");
    }
    this.picture = new Picture(picture);
    this.original = picture;
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

    if (width() == 1 || width() == 2) {
      for (int i = 0; i < width(); i++) {
        seam[i] = 0;
      }
      return seam;
    } else if (height() == 1) {
      for (int i = 0; i < width(); i++) {
        seam[i] = 0;
      }
    }

    double[][] distTo = new double[width()][height()];
    int[][] edgeTo = new int[width()][height()];

    for (int i = 0; i < width(); i++) {
      for (int j = 0; j < height(); j++) {
        if (i == 0) {
          distTo[i][j] = 1000;
        } else {
          distTo[i][j] = Double.POSITIVE_INFINITY;
        }
      }
    }

    for (int i = 0; i < width() - 1; i++) {
      for (int j = 0; j < height(); j++) {
        for (int adj = -1; adj < 2; adj++) {
          if (j + adj >= 0 && j + adj <= height() - 1) {
            if (distTo[i + 1][j + adj] > distTo[i][j] + energy(i + 1, j + adj)) {
              distTo[i + 1][j + adj] = distTo[i][j] + energy(i + 1, j + adj);
              edgeTo[i + 1][j + adj] = j * width() + i;
            }
          }
        }
      }
    }

    double shortestDistance = Double.POSITIVE_INFINITY;
    int shortestIndex = 0;

    for (int j = 0; j < height(); j++) {
      if (distTo[width() - 1][j] < shortestDistance) {
        shortestDistance = distTo[width() - 1][j];
        shortestIndex = (width() - 1) + width() * j;
      }
    }

    int currentIndex = shortestIndex;
    for (int i = width() - 1; i >= 0; i--) {
      seam[i] = currentIndex / width();
      currentIndex = edgeTo[currentIndex % width()][currentIndex / width()];
    }

    return seam;
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    int[] seam = new int[height()];

    if (height() == 1 || height() == 2) {
      for (int i = 0; i < height(); i++) {
        seam[i] = 0;
      }
      return seam;
    } else if (width() == 1) {
      for (int i = 0; i < height(); i++) {
        seam[i] = 0;
      }
    }

    double[][] distTo = new double[width()][height()];
    int[][] edgeTo = new int[width()][height()];

    for (int i = 0; i < width(); i++) {
      for (int j = 0; j < height(); j++) {
        if (j == 0) {
          distTo[i][j] = 1000;
        } else {
          distTo[i][j] = Double.POSITIVE_INFINITY;
        }
      }
    }

    for (int j = 0; j < height() - 1; j++) {
      for (int i = 0; i < width(); i++) {
        for (int adj = -1; adj < 2; adj++) {
          if (i + adj >= 0 && i + adj <= width() - 1) {
            if (distTo[i + adj][j + 1] > distTo[i][j] + energy(i + adj, j + 1)) {
              distTo[i + adj][j + 1] = distTo[i][j] + energy(i + adj, j + 1);
              edgeTo[i + adj][j + 1] = j * width() + i;
            }
          }
        }
      }
    }

    double shortestDistance = Double.POSITIVE_INFINITY;
    int shortestIndex = 0;

    for (int i = 0; i < width(); i++) {
      if (distTo[i][height() - 1] < shortestDistance) {
        shortestDistance = distTo[i][height() - 1];
        shortestIndex = i + width() * (height() - 1);
      }
    }

    int currentIndex = shortestIndex;
    for (int i = height() - 1; i >= 0; i--) {
      seam[i] = currentIndex % width();
      currentIndex = edgeTo[currentIndex % width()][currentIndex / width()];
    }

    return seam;
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Argument is null");
    } else if (seam.length != width()) {
      throw new IllegalArgumentException("Array incorrect size");
    }

    if (seam.length == 1) {
      if (seam[0] < 0 || seam[0] >= height()) {
        throw new IllegalArgumentException("Invalid seam");
      }
    }

    for (int i = 0; i < seam.length - 1; i++) {
      if (Math.abs(seam[i] - seam[i + 1]) > 1 || seam[i] < 0 || seam[i + 1] < 0 || seam[i] >= height() || seam[i + 1] >= height()) {
        throw new IllegalArgumentException("Invalid seam");
      }
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

    if (seam.length == 1) {
      if (seam[0] < 0 || seam[0] >= height()) {
        throw new IllegalArgumentException("Invalid seam");
      }
    }

    for (int i = 0; i < seam.length - 1; i++) {
      if (Math.abs(seam[i] - seam[i + 1]) > 1 || seam[i] < 0 || seam[i + 1] < 0 || seam[i] >= width() || seam[i + 1] >= width()) {
        throw new IllegalArgumentException("Invalid seam");
      }
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