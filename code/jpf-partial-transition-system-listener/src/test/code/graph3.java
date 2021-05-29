package code;
import java.util.Random;

public class graph3 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
              break;
          };
          break;
        case 1:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
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
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 4;
              break;
            case 3:
              state = 5;
              break;
          };
          break;
        case 4:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
          };
          break;
        case 5:
          switch (RANDOM.nextInt(1)) {
            case 0:
              state = 1;
              break;
          };
          break;
      }
    }
  }
}
