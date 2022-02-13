package controller.CMD;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import model.TransitionSystem;

public class TransitionSystemController {
	
	// separates the source and target of a transition
	private static final String TRANSITION_SEPARATOR = " -> ";
	
	// separates the state and its labels
	private static final String LABEL_SEPARATOR = ": ";
	
	// Parsing Regex
	private static final String TRANSITION = "-?\\d+" + TRANSITION_SEPARATOR + "\\d+";
	private static final String PARTIAL = "(\\d+\\s)*";
	private static final String STATES = "\\d+";
	
	/**
	 * Generates a partial transition system from the file with the given name.
	 * The transitions are extracted from a file named fileName.tra and the
	 * labelling of the states is extracted from a file named fileName.lab.
	 * 
	 * @param fileName the base name of the file containing the description of this
	 *                 transition system and its labelling
	 * @param deleteFiles whether or not to delete the .lab and .tra files after generation
	 * @throws IOException if something goes wrong with reading the files
	 */
	public static TransitionSystem parseTransitionSystem(String fileName, Map<String, String> inverseJNIMap, boolean deleteFiles) throws IOException {
		// for each state, its successors
		final Map<Integer, BitSet> successors;
		// for each label, its index
		final Map<String, Integer> indices;
		// for each label index, its states
		final Map<Integer, BitSet> labelling;
		// states that are partially explored
		BitSet partial;
		// number of states
		final int numberOfStates;

		// Create File objects from path
		File labFile = new File(fileName + ".lab");
		if (!labFile.exists()) {
			throw new IOException("File " + fileName + ".lab does not exist!");
		}
		File traFile = new File(fileName + ".tra");
		if (!traFile.exists()) {
			throw new IOException("File " + fileName + ".tra does not exist!");
		}

		// tra file
		Scanner input = new Scanner(traFile);
		successors = new HashMap<Integer, BitSet>();
		String line = null;
		try {
			line = input.nextLine();
			while (line.matches(TRANSITION)) { // line represents a transition
				parseTransition(line, successors);
				line = input.nextLine();
			}
		} catch (NoSuchElementException e) {
			input.close();
			throw new IOException("File " + fileName + ".tra not in the correct format");
		}
		// the one but last line contains the partially explored states
		partial = new BitSet();
		if (line.matches(PARTIAL)) {
			parsePartial(line, partial);
			line = input.nextLine();
		}
		// last line contains the number of states
		try {
			if (line.matches(STATES)) {
				numberOfStates = parseNumberOfStates(line);
			} else {
				input.close();
				throw new IOException("File " + fileName + ".tra not in the correct format");
			}
		} catch (NoSuchElementException e) {
			input.close();
			throw new IOException("File " + fileName + ".tra not in the correct format");
		}
		input.close();

		// Labelling File
		input = new Scanner(labFile);
		indices = new HashMap<String, Integer>();
		try {
			line = input.nextLine(); // first line containing the labels and their indices
			parseIndices(line, indices, inverseJNIMap);
		} catch (NoSuchElementException e) {
			input.close();
			throw new IOException("File " + fileName + ".lab not in the correct format");
		}

		labelling = new HashMap<Integer, BitSet>();
		for (int index = 0; index < indices.size(); index++) {
			labelling.put(index, new BitSet(numberOfStates));
		}
		while (input.hasNextLine()) {
			line = input.nextLine(); // line represents a state labelling
			parseLabelling(line, labelling);
		}
		input.close();

		// Attempt to cleanup
		if (deleteFiles) {
			if (!labFile.delete()) {
				throw new IOException("File " + fileName + ".lab was not deleted!");
			}
			if (!traFile.delete()) {
				throw new IOException("File " + fileName + ".tra was not deleted!");
			}
		}
		
		return new TransitionSystem(successors, indices, labelling, partial, numberOfStates);
	}
	
	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing a transition
	 */
	private static void parseTransition(String line, Map<Integer, BitSet> successors) {
		String[] part = line.split(TRANSITION_SEPARATOR);
		int source = Integer.parseInt(part[0]);
		int target = Integer.parseInt(part[1]);

		if (source != -1) { // TODO to skip -1
			BitSet post;
			if (successors.containsKey(source)) {
				post = successors.get(source);
			} else {
				post = new BitSet();
				successors.put(source, post);
			}
			post.set(target);
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing the states that are partially explored
	 */
	private static void parsePartial(String line, BitSet partial) {
		if (line.length() > 0) {
			for (String state : line.split(" ")) {
				int source = Integer.parseInt(state);
				partial.set(source);
			}
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing names of the atomic propositions and their
	 *             indices
	 */
	private static void parseIndices(String line, Map<String, Integer> indices, Map<String, String> jniMapping) {
		final String PREFIX = "true__";
		for (String item : line.split(" ")) {
			String[] pair = item.split("=");
			int index = Integer.parseInt(pair[0]);
			String label = pair[1].substring(1, pair[1].length()-1); // TODO perhaps use indexOf
			if (label.startsWith(PREFIX)) {
				label = label.substring(PREFIX.length());
				if (jniMapping == null) {
					indices.put(label, index);
				} else {
					indices.put(jniMapping.get(label), index);
				}
			}
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing a state and its labels
	 */
	private static void parseLabelling(String line, Map<Integer, BitSet> labelling) {
		String[] part = line.split(LABEL_SEPARATOR);
		int state = Integer.parseInt(part[0]);
		if (state != -1) {
			for (String label : part[1].split(" ")) {
				int index = Integer.parseInt(label);
				BitSet set = labelling.get(index); // TODO modified this
				if (set != null) {
					set.set(state);
				}
			}
		}
	}

	/**
	 * Parses the given line.
	 * 
	 * @param line a line representing the number of states
	 */
	private static Integer parseNumberOfStates(String line) {
		return Integer.parseInt(line);
	}
}
