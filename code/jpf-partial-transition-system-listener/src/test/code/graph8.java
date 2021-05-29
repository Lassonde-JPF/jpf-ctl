package code;
import java.util.Random;

public class graph8 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          switch (RANDOM.nextInt(5)) {
            case 0:
              state = 1;
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
            case 4:
              state = 6;
              break;
          };
          break;
        case 1:
          done = true;
          break;
        case 2:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 7;
              break;
            case 1:
              state = 8;
              break;
          };
          break;
        case 3:
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 4;
              break;
            case 2:
              state = 5;
              break;
            case 3:
              state = 6;
              break;
          };
          break;
        case 4:
          switch (RANDOM.nextInt(5)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 5;
              break;
            case 3:
              state = 7;
              break;
            case 4:
              state = 8;
              break;
          };
          break;
        case 5:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 5;
              break;
            case 1:
              state = 8;
              break;
          };
          break;
        case 6:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 4;
              break;
          };
          break;
        case 7:
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 4;
              break;
            case 3:
              state = 7;
              break;
          };
          break;
        case 8:
          switch (RANDOM.nextInt(2)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 4;
              break;
          };
          break;
      }
    }
  }
}
