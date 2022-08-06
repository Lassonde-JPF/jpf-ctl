/*
 * Copyright (C)  2022
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jpf.logic;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.search.SearchListenerAdapter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Add a partial state space observer to JPF and build a graph of the state
 * space that is explored by JPF, as well as any unexplored states.
 *
 * @see gov.nasa.jpf.JPFListener
 *
 * @author Richard Robinson
 * @author Matt Walker
 */
public class PartialTransitionSystemListener extends SearchListenerAdapter {
	private final Map<Integer, Set<Integer>> transitions;
	private final Set<Integer> unexploredStates;

	private PrintWriter writer;

	private int source;
	private int target;

	private int numberOfStates;
	private int numberOfTransitions;

	/**
	 * Initializes this partial transition system as empty (no states and no transitions).
	 * 
	 * @param config a configuration
	 */
	public PartialTransitionSystemListener(Config config) {
		this.transitions = new LinkedHashMap<>();
		this.unexploredStates = new TreeSet<>();

		this.source = -1;
		this.target = -1;

		this.numberOfStates = 0;
		this.numberOfTransitions = 0;
	}

	/**
	 * Invoked when the search is started.
	 *
	 * @implNote Creates and instantiates a {@code PrintWriter} to be used for
	 *           output. The path of the outputted file is the SUT name of the VM
	 *           concatenated with {@code .tra}.
	 *
	 * @param search the Search instance
	 */
	public void searchStarted(Search search) {
		String name = search.getVM().getSUTName() + ".tra"; //TODO revert this
		try {
			this.writer = new PrintWriter(name);
		} catch (FileNotFoundException e) {
			System.out.println("Listener could not write to file " + name);
			search.terminate();
		}
	}

	/**
	 * Invoked when a state has advanced.
	 *
	 * @implNote The output is not processed in this method. Instead, the method
	 *           adds the source and the target to a private MultiMap field (a map
	 *           whose keys are the source states and whose values are a list of all
	 *           the targets reached from the source).
	 *
	 *           <p>
	 *           </p>
	 *           In addition, this method does additional logic depending on if the
	 *           state is an end state or a new state, or if the number of new
	 *           states has been exceeded. In the latter case, the search
	 *           terminates.
	 *
	 * @param search the Search instance
	 */
	@Override
	public void stateAdvanced(Search search) {
		this.source = this.target;
		this.target = search.getStateId();

		if (this.source != -1) {
			if (!this.transitions.containsKey(this.source)) {
				this.transitions.put(this.source, new LinkedHashSet<>());
			}
			if (this.transitions.get(this.source).add(this.target)) {
				this.numberOfTransitions++;
			}
		}
		
		if (search.isNewState()) {
			this.numberOfStates++;
			this.unexploredStates.add(this.target);
		}
		if (search.isEndState()) {
			this.unexploredStates.remove(this.target);
		}
	}

	/**
	 * Invoked when the search has finished.
	 *
	 * @implNote This method prints a formatted version of the transitions recorded
	 *           in the stateAdvanced method as well as the set of unexplored
	 *           states.
	 *
	 * @param search - the Search instance
	 */
	public void searchFinished(Search search) {
		this.write();
	}

	@Override
	public void stateBacktracked(Search search) {
		this.target = search.getStateId();
	}

	@Override
	public void stateRestored(Search search) {
		this.target = search.getStateId();
	}

	/**
	 * Invoked when a state is processed.
	 * 
	 * @implNote When `search.getStateId()` has been processed by JPF, it is removed
	 *           from the set of unexploredStates.
	 * 
	 * @param search - the Search instance
	 */
	@Override
	public void stateProcessed(Search search) {
		this.unexploredStates.remove(search.getStateId());
	}

	@Override
	public void searchConstraintHit(Search search) {
		this.write();
	}

	/**
	 * Writes this partial transition system to file.
	 */
	private void write() {
		writer.printf("%d %d%n", this.numberOfStates, this.numberOfTransitions);

		for (Map.Entry<Integer, Set<Integer>> entry : this.transitions.entrySet()) {
			int source = entry.getKey();
			Set<Integer> targets = entry.getValue();

			for (int target : targets) {
				writer.printf("%d -> %d%n", source, target);
			}
		}

		StringJoiner statesList = new StringJoiner(" ");
		for (int state : this.unexploredStates) {
			statesList.add("" + state);
		}
		writer.println(statesList.toString());

		this.writer.close();
	}
}