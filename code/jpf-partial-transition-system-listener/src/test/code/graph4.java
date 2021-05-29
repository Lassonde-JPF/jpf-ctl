package code;
import java.util.Random;

public class graph4 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          switch (RANDOM.nextInt(7)) {
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
            case 4:
              state = 8;
              break;
            case 5:
              state = 9;
              break;
            case 6:
              state = 10;
              break;
          };
          break;
        case 1:
          switch (RANDOM.nextInt(3)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 5;
              break;
          };
          break;
        case 2:
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 7;
              break;
            case 2:
              state = 8;
              break;
            case 3:
              state = 9;
              break;
          };
          break;
        case 3:
          switch (RANDOM.nextInt(7)) {
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
            case 5:
              state = 8;
              break;
            case 6:
              state = 10;
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
              state = 4;
              break;
            case 3:
              state = 6;
              break;
            case 4:
              state = 9;
              break;
          };
          break;
        case 5:
          switch (RANDOM.nextInt(6)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 7;
              break;
            case 4:
              state = 8;
              break;
            case 5:
              state = 9;
              break;
          };
          break;
        case 6:
          switch (RANDOM.nextInt(5)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 8;
              break;
            case 3:
              state = 9;
              break;
            case 4:
              state = 10;
              break;
          };
          break;
        case 7:
          switch (RANDOM.nextInt(1)) {
            case 0:
              state = 4;
              break;
          };
          break;
        case 8:
          switch (RANDOM.nextInt(5)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 8;
              break;
            case 4:
              state = 9;
              break;
          };
          break;
        case 9:
          switch (RANDOM.nextInt(3)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 6;
              break;
          };
          break;
        case 10:
          switch (RANDOM.nextInt(3)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 4;
              break;
          };
          break;
      }
    }
  }
}
