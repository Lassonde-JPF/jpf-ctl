package controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import formulas.Formula;
import model.LogicType;
import model.ModelChecker;
import model.Result;
import model.TransitionSystem;
import model.ctl.CTLModelChecker;

/**
 * Model Checking Manager.
 * 
 * @author Matthew Walker
 * @author Franck van Breugel
 */
public class Manager {

	/**
	 * Private worker class for model checking
	 * 
	 * @author Matthew Walker
	 * @author Franck van Breugel
	 */
	private class Worker implements Callable<Result> {

		// Attributes
		private Formula formula;
		private ModelChecker checker;

		/**
		 * Initializes a Worker with an alias, formula, and checker.
		 * 
		 * @param alias   - alias of a formula
		 * @param formula - formula object to use in model checking
		 * @param checker - model checker for use in calculation
		 */
		public Worker(String alias, Formula formula, ModelChecker checker) {
			super();
			this.formula = formula;
			this.checker = checker;
		}

		/**
		 * Action to be executed by this Worker
		 * 
		 * @return Result - a model checking result
		 */
		@Override
		public Result call() throws Exception {
			return this.checker.check(this.formula);
		}

	}

	// Attributes
	private Map<String, Formula> formulas;
	private CTLModelChecker checker;

	/**
	 * Initializes this Manager with a transition system, map of alias -> formulas,
	 * and a given logic type.
	 * 
	 * @param pts      - TransitionSystem to be model checked
	 * @param formulas - Map of alias' -> formulas to be model checked
	 * @param type     - Type of Logic to consider for model checking
	 */
	public Manager(TransitionSystem pts, Map<String, Formula> formulas, LogicType type) {
		this.formulas = formulas;
		switch (type) {
		case CTL:
			this.checker = new CTLModelChecker(pts);
		}
	}

	/**
	 * Validates each formula on supplied transition system, sequentially.
	 * 
	 * @return Map<String, Result> - Map of formula alias' to results for all
	 *         formulas.
	 */
	public Map<String, Result> validateSequentially() {
		return this.formulas.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> this.checker.check(e.getValue())));
	}

	/**
	 * Validates each formula on supplied transition system, in parallel.
	 * 
	 * @param timeout - maximum timeout to apply on each formula
	 * @param unit    - time unit to apply to timeout
	 * @return Map<String, Result> - Map of formula alias' to results for all
	 *         formulas.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public Map<String, Result> validateParallel(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		// Construct thread executor pool
		ExecutorService pool = Executors.newCachedThreadPool();
		// Contruct temporary structure to store future results
		Map<String, Future<Result>> futureMap = new HashMap<>();
		// Spawn threads
		for (Entry<String, Formula> entry : this.formulas.entrySet()) {
			futureMap.computeIfAbsent(entry.getKey(),
					k -> pool.submit(new Worker(entry.getKey(), entry.getValue(), this.checker)));
		}
		// Collect threads
		Map<String, Result> results = new HashMap<>();
		for (Entry<String, Future<Result>> entry : futureMap.entrySet()) {
			results.put(entry.getKey(), entry.getValue().get(timeout, unit));
		}
		return results;
	}

}
