package code;
import java.util.Random;

public class graph0 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          switch (RANDOM.nextInt(8)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 4;
              break;
            case 2:
              state = 7;
              break;
            case 3:
              state = 9;
              break;
            case 4:
              state = 10;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 12;
              break;
            case 7:
              state = 14;
              break;
          };
          break;
        case 1:
          switch (RANDOM.nextInt(8)) {
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
            case 4:
              state = 8;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 12;
              break;
            case 7:
              state = 14;
              break;
          };
          break;
        case 2:
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 9;
              break;
            case 3:
              state = 13;
              break;
          };
          break;
        case 3:
          switch (RANDOM.nextInt(7)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 7;
              break;
            case 3:
              state = 8;
              break;
            case 4:
              state = 9;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 15;
              break;
          };
          break;
        case 4:
          switch (RANDOM.nextInt(7)) {
            case 0:
              state = 6;
              break;
            case 1:
              state = 8;
              break;
            case 2:
              state = 9;
              break;
            case 3:
              state = 10;
              break;
            case 4:
              state = 12;
              break;
            case 5:
              state = 13;
              break;
            case 6:
              state = 14;
              break;
          };
          break;
        case 5:
          switch (RANDOM.nextInt(10)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 4;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 7;
              break;
            case 4:
              state = 9;
              break;
            case 5:
              state = 10;
              break;
            case 6:
              state = 11;
              break;
            case 7:
              state = 12;
              break;
            case 8:
              state = 13;
              break;
            case 9:
              state = 15;
              break;
          };
          break;
        case 6:
          switch (RANDOM.nextInt(6)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 4;
              break;
            case 2:
              state = 7;
              break;
            case 3:
              state = 9;
              break;
            case 4:
              state = 13;
              break;
            case 5:
              state = 14;
              break;
          };
          break;
        case 7:
          switch (RANDOM.nextInt(5)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 3;
              break;
            case 3:
              state = 6;
              break;
            case 4:
              state = 10;
              break;
          };
          break;
        case 8:
          switch (RANDOM.nextInt(4)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 8;
              break;
            case 2:
              state = 10;
              break;
            case 3:
              state = 11;
              break;
          };
          break;
        case 9:
          switch (RANDOM.nextInt(7)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 4;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 8;
              break;
            case 4:
              state = 10;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 14;
              break;
          };
          break;
        case 10:
          switch (RANDOM.nextInt(7)) {
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
              state = 10;
              break;
            case 4:
              state = 11;
              break;
            case 5:
              state = 12;
              break;
            case 6:
              state = 14;
              break;
          };
          break;
        case 11:
          switch (RANDOM.nextInt(8)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 5;
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
            case 6:
              state = 11;
              break;
            case 7:
              state = 12;
              break;
          };
          break;
        case 12:
          switch (RANDOM.nextInt(8)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 5;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 9;
              break;
            case 4:
              state = 11;
              break;
            case 5:
              state = 12;
              break;
            case 6:
              state = 13;
              break;
            case 7:
              state = 15;
              break;
          };
          break;
        case 13:
          switch (RANDOM.nextInt(6)) {
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
              state = 9;
              break;
            case 5:
              state = 12;
              break;
          };
          break;
        case 14:
          switch (RANDOM.nextInt(5)) {
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
              state = 11;
              break;
            case 4:
              state = 12;
              break;
          };
          break;
        case 15:
          switch (RANDOM.nextInt(5)) {
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
              state = 12;
              break;
            case 4:
              state = 13;
              break;
          };
          break;
      }
    }
  }
}
