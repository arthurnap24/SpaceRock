import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sean on 2/4/17.
 */
public class Camera implements Sensorable {
  private static final int[] image_size = {4000,4000};
  private static final int MAX_ASTEROIDS = 5;
  private HashMap<Integer, Picture> images = new HashMap<>();
  private int elapsed_seconds = 0;
  private ArrayList<Asteroid> asteroids = new ArrayList<>();

  /**
   * Initialize a camera object
   */
  public Camera() {
    this.images = new HashMap<Integer, Picture>();
  }

  /**
   * Returns the status of the Camera.
   *
   * @return
   */
  public int status() {
    return 0;
  }

  /**
   * Notifies us to take a picture with a zoom level (1-3) ??
   * Returns a generated id
   *
   * @param zoom
   * @return
   */
  public int take_picture(int zoom) {
    Picture picture = generate_image(elapsed_seconds, zoom);
    images.put(picture.id, picture);
    return picture.id;
  }

  public int take_picture() {
    return take_picture(0);
  }

  public void set_elapsed_seconds(int elapsed_seconds) {
    this.elapsed_seconds = elapsed_seconds;
  }

  /**
   * Returns a chunk of an image with the given id
   *
   * @param id
   * @param x
   * @param y
   * @param size
   * @return
   */
  public Image image_chunk(int id, int x, int y, int size) {
    Picture picture = images.get(id);
    return picture.chunk(x, y, size);
  }

  public Picture getPicture(int id) {
    return images.get(id);
  }

  /**
   * Generate a image at the given time with the given zoom
   *
   * @param time
   * @param zoom
   * @return
   */
  private Picture generate_image(int time, int zoom) {
    BufferedImage image = new BufferedImage(image_size[0], image_size[1], BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.black);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());

    if (elapsed_seconds == 0) {
      asteroids.add(new Asteroid(new int[]{200, 200, 500}, 500, new int[]{1, 1, -1}));
      generate_random_asteroids();
    }

    for (Asteroid asteroid : asteroids) {
      asteroid.move(time);
      draw_asteroid_to_image(image, asteroid);
    }

    return new Picture(image);
  }

  private void generate_random_asteroids() {
    while(asteroids.size() <= MAX_ASTEROIDS) {
      asteroids.add(new Asteroid(image_size));
    }
  }

  private void draw_asteroid_to_image(BufferedImage image, Asteroid asteroid) {
    if (asteroid.current_location[2] <= 0) {
      System.out.println("Asteroid is now behind us.");
      return;
    }
    int xmin = Math.max(asteroid.current_location[0] - asteroid.current_radius, 0);
    int xmax = Math.min(asteroid.current_location[0] + asteroid.current_radius, image.getWidth());
    int ymin = Math.max(asteroid.current_location[1] + asteroid.current_radius, 0);
    int ymax = Math.min(asteroid.current_location[1] + asteroid.current_radius, image.getHeight());

    Graphics2D g = (Graphics2D) image.createGraphics();
    g.setColor(Color.gray);
    g.fillOval(asteroid.current_location[0], asteroid.current_location[1], asteroid.current_radius, asteroid.current_radius);
  }

  public static void main(String[] args) {
    Camera camera = new Camera();

    for (int i = 0; i < 10; i++) {
      camera.set_elapsed_seconds(i * 30);
      int picture_id = camera.take_picture();
      System.out.format("picture %d was taken\n", picture_id);
      Picture picture = camera.getPicture(picture_id);
      File output_file = new File("generated_image_" + picture.id + ".png");
      try {
        ImageIO.write(picture.get_image(), "png", output_file);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.format("Output picture %d to generated_image_%d.png\n", picture_id, picture_id);
    }

  }
}
