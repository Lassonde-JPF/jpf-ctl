package partialtransitionsystemlistener;

import java.io.PrintWriter;
import java.util.*;

class TRAListener implements PartialStateSpacePrinter {
    @Override
    public String getFileName(String sutName) {
        return sutName + ".tra";
    }

    @Override
    public void printResult(Map<Integer, Set<Integer>> transitions, Set<Integer> unexploredStates, PrintWriter writer) {

        for (Map.Entry<Integer, Set<Integer>> entry : transitions.entrySet()) {
            int source = entry.getKey();
            Set<Integer> targets = entry.getValue();

            for (int target : targets) {
                writer.printf("%d -> %d%n", source, target);
            }
        }

        StringJoiner sj = new StringJoiner(" ");
        for (int state : unexploredStates) {
        		sj.add("" + state);
        }
        
        writer.printf(sj.toString());
    }
}
