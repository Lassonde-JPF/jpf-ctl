package code;
import java.util.Random;
import partialtransitionsystemlistener.Graph;

public class Graph0 implements Graph {
  public void run() {
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
              state = 2;
              break;
          };
          break;
        case 1:
          switch (RANDOM.nextInt(3)) {
            case 0:
              state = 1;
              break;
            case 1:
              state = 2;
              break;
            case 2:
              state = 3;
              break;
          };
          break;
        case 2:
          done = true;
          break;
        case 3:
          done = true;
          break;
        case 4:
          done = true;
          break;
      }
    }
  }
}
