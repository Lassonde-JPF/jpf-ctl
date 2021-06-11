package partialtransitionsystemlistener;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

interface PartialStateSpacePrinter {
    void printResult(Map<Integer, Set<Integer>> transitions, Set<Integer> unexploredStates, PrintWriter writer);

    String getFileName(String sutName);
}
