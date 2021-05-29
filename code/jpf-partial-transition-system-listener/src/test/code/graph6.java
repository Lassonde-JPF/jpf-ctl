package code;
import java.util.Random;

public class graph6 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          done = true;
          break;
        case 1:
          switch (RANDOM.nextInt(1)) {
            case 0:
              state = 1;
              break;
          };
          break;
        case 2:
          switch (RANDOM.nextInt(1)) {
            case 0:
              state = 1;
              break;
          };
          break;
        case 3:
          done = true;
          break;
      }
    }
  }
}
