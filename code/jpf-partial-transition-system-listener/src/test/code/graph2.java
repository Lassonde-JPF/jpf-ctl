package code;
import java.util.Random;

public class graph2 {
  public static void main(String[] args) {
    final Random RANDOM = new Random();
    boolean done = false;
    int state = 0;
    while (!done) {
      switch (state) {
        case 0:
          switch (RANDOM.nextInt(14)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 5;
              break;
            case 2:
              state = 8;
              break;
            case 3:
              state = 10;
              break;
            case 4:
              state = 13;
              break;
            case 5:
              state = 15;
              break;
            case 6:
              state = 17;
              break;
            case 7:
              state = 18;
              break;
            case 8:
              state = 21;
              break;
            case 9:
              state = 22;
              break;
            case 10:
              state = 24;
              break;
            case 11:
              state = 25;
              break;
            case 12:
              state = 26;
              break;
            case 13:
              state = 27;
              break;
          };
          break;
        case 1:
          switch (RANDOM.nextInt(12)) {
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
              state = 11;
              break;
            case 5:
              state = 12;
              break;
            case 6:
              state = 18;
              break;
            case 7:
              state = 19;
              break;
            case 8:
              state = 20;
              break;
            case 9:
              state = 23;
              break;
            case 10:
              state = 24;
              break;
            case 11:
              state = 27;
              break;
          };
          break;
        case 2:
          switch (RANDOM.nextInt(12)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 7;
              break;
            case 2:
              state = 10;
              break;
            case 3:
              state = 12;
              break;
            case 4:
              state = 15;
              break;
            case 5:
              state = 16;
              break;
            case 6:
              state = 17;
              break;
            case 7:
              state = 18;
              break;
            case 8:
              state = 19;
              break;
            case 9:
              state = 20;
              break;
            case 10:
              state = 21;
              break;
            case 11:
              state = 27;
              break;
          };
          break;
        case 3:
          switch (RANDOM.nextInt(15)) {
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
              state = 5;
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
              state = 17;
              break;
            case 8:
              state = 18;
              break;
            case 9:
              state = 19;
              break;
            case 10:
              state = 20;
              break;
            case 11:
              state = 21;
              break;
            case 12:
              state = 22;
              break;
            case 13:
              state = 23;
              break;
            case 14:
              state = 25;
              break;
          };
          break;
        case 4:
          switch (RANDOM.nextInt(15)) {
            case 0:
              state = 2;
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
            case 5:
              state = 11;
              break;
            case 6:
              state = 12;
              break;
            case 7:
              state = 13;
              break;
            case 8:
              state = 14;
              break;
            case 9:
              state = 19;
              break;
            case 10:
              state = 20;
              break;
            case 11:
              state = 22;
              break;
            case 12:
              state = 23;
              break;
            case 13:
              state = 24;
              break;
            case 14:
              state = 25;
              break;
          };
          break;
        case 5:
          switch (RANDOM.nextInt(14)) {
            case 0:
              state = 5;
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
            case 4:
              state = 10;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 13;
              break;
            case 7:
              state = 14;
              break;
            case 8:
              state = 18;
              break;
            case 9:
              state = 19;
              break;
            case 10:
              state = 20;
              break;
            case 11:
              state = 21;
              break;
            case 12:
              state = 22;
              break;
            case 13:
              state = 24;
              break;
          };
          break;
        case 6:
          switch (RANDOM.nextInt(14)) {
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
              state = 7;
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
            case 7:
              state = 14;
              break;
            case 8:
              state = 15;
              break;
            case 9:
              state = 16;
              break;
            case 10:
              state = 18;
              break;
            case 11:
              state = 21;
              break;
            case 12:
              state = 22;
              break;
            case 13:
              state = 23;
              break;
          };
          break;
        case 7:
          switch (RANDOM.nextInt(9)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 6;
              break;
            case 3:
              state = 7;
              break;
            case 4:
              state = 12;
              break;
            case 5:
              state = 16;
              break;
            case 6:
              state = 22;
              break;
            case 7:
              state = 24;
              break;
            case 8:
              state = 26;
              break;
          };
          break;
        case 8:
          switch (RANDOM.nextInt(11)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 5;
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
              state = 14;
              break;
            case 6:
              state = 18;
              break;
            case 7:
              state = 22;
              break;
            case 8:
              state = 23;
              break;
            case 9:
              state = 25;
              break;
            case 10:
              state = 27;
              break;
          };
          break;
        case 9:
          switch (RANDOM.nextInt(16)) {
            case 0:
              state = 3;
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
              state = 14;
              break;
            case 10:
              state = 16;
              break;
            case 11:
              state = 19;
              break;
            case 12:
              state = 21;
              break;
            case 13:
              state = 23;
              break;
            case 14:
              state = 26;
              break;
            case 15:
              state = 27;
              break;
          };
          break;
        case 10:
          switch (RANDOM.nextInt(14)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
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
            case 5:
              state = 11;
              break;
            case 6:
              state = 13;
              break;
            case 7:
              state = 15;
              break;
            case 8:
              state = 16;
              break;
            case 9:
              state = 17;
              break;
            case 10:
              state = 20;
              break;
            case 11:
              state = 25;
              break;
            case 12:
              state = 26;
              break;
            case 13:
              state = 27;
              break;
          };
          break;
        case 11:
          switch (RANDOM.nextInt(8)) {
            case 0:
              state = 2;
              break;
            case 1:
              state = 3;
              break;
            case 2:
              state = 5;
              break;
            case 3:
              state = 6;
              break;
            case 4:
              state = 11;
              break;
            case 5:
              state = 17;
              break;
            case 6:
              state = 23;
              break;
            case 7:
              state = 24;
              break;
          };
          break;
        case 12:
          switch (RANDOM.nextInt(15)) {
            case 0:
              state = 2;
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
            case 4:
              state = 7;
              break;
            case 5:
              state = 8;
              break;
            case 6:
              state = 9;
              break;
            case 7:
              state = 11;
              break;
            case 8:
              state = 13;
              break;
            case 9:
              state = 15;
              break;
            case 10:
              state = 16;
              break;
            case 11:
              state = 17;
              break;
            case 12:
              state = 23;
              break;
            case 13:
              state = 25;
              break;
            case 14:
              state = 27;
              break;
          };
          break;
        case 13:
          switch (RANDOM.nextInt(15)) {
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
              state = 6;
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
            case 7:
              state = 14;
              break;
            case 8:
              state = 15;
              break;
            case 9:
              state = 16;
              break;
            case 10:
              state = 19;
              break;
            case 11:
              state = 20;
              break;
            case 12:
              state = 22;
              break;
            case 13:
              state = 24;
              break;
            case 14:
              state = 25;
              break;
          };
          break;
        case 14:
          switch (RANDOM.nextInt(14)) {
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
              state = 11;
              break;
            case 4:
              state = 12;
              break;
            case 5:
              state = 13;
              break;
            case 6:
              state = 17;
              break;
            case 7:
              state = 18;
              break;
            case 8:
              state = 19;
              break;
            case 9:
              state = 21;
              break;
            case 10:
              state = 23;
              break;
            case 11:
              state = 24;
              break;
            case 12:
              state = 25;
              break;
            case 13:
              state = 27;
              break;
          };
          break;
        case 15:
          switch (RANDOM.nextInt(15)) {
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
              state = 7;
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
            case 8:
              state = 13;
              break;
            case 9:
              state = 14;
              break;
            case 10:
              state = 16;
              break;
            case 11:
              state = 19;
              break;
            case 12:
              state = 20;
              break;
            case 13:
              state = 22;
              break;
            case 14:
              state = 25;
              break;
          };
          break;
        case 16:
          switch (RANDOM.nextInt(17)) {
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
              state = 4;
              break;
            case 4:
              state = 6;
              break;
            case 5:
              state = 8;
              break;
            case 6:
              state = 11;
              break;
            case 7:
              state = 12;
              break;
            case 8:
              state = 14;
              break;
            case 9:
              state = 16;
              break;
            case 10:
              state = 18;
              break;
            case 11:
              state = 19;
              break;
            case 12:
              state = 20;
              break;
            case 13:
              state = 22;
              break;
            case 14:
              state = 23;
              break;
            case 15:
              state = 25;
              break;
            case 16:
              state = 27;
              break;
          };
          break;
        case 17:
          switch (RANDOM.nextInt(14)) {
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
              state = 7;
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
              state = 15;
              break;
            case 8:
              state = 17;
              break;
            case 9:
              state = 18;
              break;
            case 10:
              state = 20;
              break;
            case 11:
              state = 22;
              break;
            case 12:
              state = 24;
              break;
            case 13:
              state = 25;
              break;
          };
          break;
        case 18:
          switch (RANDOM.nextInt(14)) {
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
              state = 7;
              break;
            case 4:
              state = 9;
              break;
            case 5:
              state = 11;
              break;
            case 6:
              state = 12;
              break;
            case 7:
              state = 13;
              break;
            case 8:
              state = 14;
              break;
            case 9:
              state = 15;
              break;
            case 10:
              state = 17;
              break;
            case 11:
              state = 18;
              break;
            case 12:
              state = 20;
              break;
            case 13:
              state = 25;
              break;
          };
          break;
        case 19:
          switch (RANDOM.nextInt(10)) {
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
              state = 10;
              break;
            case 4:
              state = 11;
              break;
            case 5:
              state = 16;
              break;
            case 6:
              state = 20;
              break;
            case 7:
              state = 21;
              break;
            case 8:
              state = 22;
              break;
            case 9:
              state = 24;
              break;
          };
          break;
        case 20:
          switch (RANDOM.nextInt(11)) {
            case 0:
              state = 3;
              break;
            case 1:
              state = 8;
              break;
            case 2:
              state = 13;
              break;
            case 3:
              state = 15;
              break;
            case 4:
              state = 17;
              break;
            case 5:
              state = 19;
              break;
            case 6:
              state = 21;
              break;
            case 7:
              state = 22;
              break;
            case 8:
              state = 23;
              break;
            case 9:
              state = 25;
              break;
            case 10:
              state = 27;
              break;
          };
          break;
        case 21:
          switch (RANDOM.nextInt(16)) {
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
              state = 5;
              break;
            case 4:
              state = 6;
              break;
            case 5:
              state = 7;
              break;
            case 6:
              state = 8;
              break;
            case 7:
              state = 9;
              break;
            case 8:
              state = 12;
              break;
            case 9:
              state = 16;
              break;
            case 10:
              state = 18;
              break;
            case 11:
              state = 20;
              break;
            case 12:
              state = 21;
              break;
            case 13:
              state = 22;
              break;
            case 14:
              state = 23;
              break;
            case 15:
              state = 25;
              break;
          };
          break;
        case 22:
          switch (RANDOM.nextInt(14)) {
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
            case 5:
              state = 12;
              break;
            case 6:
              state = 13;
              break;
            case 7:
              state = 14;
              break;
            case 8:
              state = 15;
              break;
            case 9:
              state = 17;
              break;
            case 10:
              state = 19;
              break;
            case 11:
              state = 20;
              break;
            case 12:
              state = 22;
              break;
            case 13:
              state = 27;
              break;
          };
          break;
        case 23:
          switch (RANDOM.nextInt(10)) {
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
              state = 9;
              break;
            case 4:
              state = 12;
              break;
            case 5:
              state = 16;
              break;
            case 6:
              state = 17;
              break;
            case 7:
              state = 18;
              break;
            case 8:
              state = 19;
              break;
            case 9:
              state = 23;
              break;
          };
          break;
        case 24:
          switch (RANDOM.nextInt(14)) {
            case 0:
              state = 2;
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
            case 4:
              state = 11;
              break;
            case 5:
              state = 14;
              break;
            case 6:
              state = 15;
              break;
            case 7:
              state = 16;
              break;
            case 8:
              state = 17;
              break;
            case 9:
              state = 21;
              break;
            case 10:
              state = 22;
              break;
            case 11:
              state = 23;
              break;
            case 12:
              state = 24;
              break;
            case 13:
              state = 26;
              break;
          };
          break;
        case 25:
          switch (RANDOM.nextInt(8)) {
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
              state = 7;
              break;
            case 5:
              state = 9;
              break;
            case 6:
              state = 14;
              break;
            case 7:
              state = 15;
              break;
          };
          break;
        case 26:
          switch (RANDOM.nextInt(12)) {
            case 0:
              state = 4;
              break;
            case 1:
              state = 7;
              break;
            case 2:
              state = 8;
              break;
            case 3:
              state = 11;
              break;
            case 4:
              state = 12;
              break;
            case 5:
              state = 13;
              break;
            case 6:
              state = 16;
              break;
            case 7:
              state = 17;
              break;
            case 8:
              state = 18;
              break;
            case 9:
              state = 22;
              break;
            case 10:
              state = 23;
              break;
            case 11:
              state = 27;
              break;
          };
          break;
        case 27:
          switch (RANDOM.nextInt(16)) {
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
              state = 7;
              break;
            case 6:
              state = 8;
              break;
            case 7:
              state = 10;
              break;
            case 8:
              state = 15;
              break;
            case 9:
              state = 16;
              break;
            case 10:
              state = 19;
              break;
            case 11:
              state = 21;
              break;
            case 12:
              state = 24;
              break;
            case 13:
              state = 25;
              break;
            case 14:
              state = 26;
              break;
            case 15:
              state = 27;
              break;
          };
          break;
      }
    }
  }
}
