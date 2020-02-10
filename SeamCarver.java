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
    EdgeWeightedDigraph horizontaldG = findHorizontalDG();

    double minimumDistance = Double.POSITIVE_INFINITY;
    int start = 0;
    int end = 0;
    for (int i = 0; i < height(); i++) {
      AcyclicSP sp = new AcyclicSP(horizontaldG, i);
      for (int j = 0; j < width(); j++) {
        if (sp.distTo(width() * height() - 1 - j) < minimumDistance) {
          minimumDistance = sp.distTo(width() * height() - 1 - j);
          start = i;
          end = width() * height() - 1 - j;
        }
      }
    }
    AcyclicSP sp = new AcyclicSP(horizontaldG, start);
    int[] seams = new int[width()];
    int count = 0;
    for (DirectedEdge d : sp.pathTo(end)) {
      seams[count] = d.from() % height();
      count++;
    }
    seams[width() - 1] = end % height();
    return seams;
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    EdgeWeightedDigraph verticaldG = findVerticalDG();
    double minimumDistance = Double.POSITIVE_INFINITY;
    int start = 0;
    int end = 0;
    for (int i = 0; i < width(); i++) {
    AcyclicSP sp = new AcyclicSP(verticaldG, i);
      for (int j = 0; j < width(); j++) {
        if (sp.distTo(width() * height() - 1 - j) < minimumDistance) {
          minimumDistance = sp.distTo(width() * height() - 1 - j);
          start = i;
          end = width() * height() - 1 - j;
        }
      }
    }
    AcyclicSP sp = new AcyclicSP(verticaldG, start);
    int[] seams = new int[height()];
    int count = 0;
    for (DirectedEdge d : sp.pathTo(end)) {
      seams[count] = d.from() % width();
      count++;
    }
    seams[height() - 1] = end % width();
    return seams;
  }

  private EdgeWeightedDigraph findVerticalDG() {
    EdgeWeightedDigraph verticaldG = new EdgeWeightedDigraph(width() * height());
    for (int i = 0; i < width() * (height() - 1); i++) {
      verticaldG.addEdge(new DirectedEdge(i, i + width(), energy((i + width()) % width(), (i + width()) / width())));
      if (i % width() == 0) {
        verticaldG.addEdge(new DirectedEdge(i, i + width() + 1, energy((i + width() + 1) % width(), (i + width() + 1) / width())));
      } else if (i % width() == width() - 1) {
        verticaldG.addEdge(new DirectedEdge(i, i + width() - 1, energy((i + width() - 1) % width(), (i + width() - 1) / width())));

      } else {
        verticaldG.addEdge(new DirectedEdge(i, i + width() + 1, energy((i + width() + 1) % width(), (i + width() + 1) / width())));
        verticaldG.addEdge(new DirectedEdge(i, i + width() - 1, energy((i + width() - 1) % width(), (i + width() - 1) / width())));
      }
    }
    return verticaldG;
  }

  private EdgeWeightedDigraph findHorizontalDG() {
    EdgeWeightedDigraph horizontaldG = new EdgeWeightedDigraph(width() * height());
    for (int i = 0; i < height() * (width() - 1); i++) {
      horizontaldG.addEdge(new DirectedEdge(i, i + height(), energy((i + height()) % height(), (i + height()) / height())));
      if (i % height() == 0) {
        horizontaldG.addEdge(new DirectedEdge(i, i + height() + 1, energy((i + height() + 1) % height(), (i + height() + 1) / height())));
      } else if (i % height() == height() - 1) {
        horizontaldG.addEdge(new DirectedEdge(i, i + height() - 1, energy((i + height() - 1) % height(), (i + height() - 1) / height())));
      } else {
        horizontaldG.addEdge(new DirectedEdge(i, i + height() + 1, energy((i + height() + 1) % height(), (i + height() + 1) / height())));
        horizontaldG.addEdge(new DirectedEdge(i, i + height() - 1, energy((i + height() - 1) % height(), (i + height() - 1) / height())));
      }
    }
    return horizontaldG;
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Argument is null");
    } else if (seam.length != width()) {
      throw new IllegalArgumentException("Array incorrect size");
    }
    Picture oldPicture = picture;
    picture = new Picture(width(), height() - 1);
    for (int x = 0; x < height(); x++) {
      for (int y = 0; y < width(); y++) {
        if (x < seam[y]) {
          picture.setRGB(x, y, oldPicture.getRGB(x, y));
        } else if (x > seam[y]) {
          picture.setRGB(x, y, oldPicture.getRGB(x, y + 1));
        }
      }
    }
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Argument is null");
    } else if (seam.length != height()) {
      throw new IllegalArgumentException("Array incorrect size");
    }
    Picture oldPicture = picture;
    picture = new Picture(width() - 1, height());
    for (int x = 0; x < width(); x++) {
      for (int y = 0; y < height(); y++) {
        if (x < seam[y]) {
          picture.setRGB(x, y, oldPicture.getRGB(x, y));
        } else if (x > seam[y]) {
          picture.setRGB(x, y, oldPicture.getRGB(x + 1, y));
        }
      }
    }
  }

  //  unit testing (optional)
  public static void main(String[] args) {
    //Testing
  }

}