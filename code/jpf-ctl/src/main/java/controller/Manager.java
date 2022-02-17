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

public class Manager {

	private class Worker implements Callable<Result> {

		private Formula formula;
		private ModelChecker checker;

		public Worker(String alias, Formula formula, ModelChecker checker) {
			super();
			this.formula = formula;
			this.checker = checker;
		}

		@Override
		public Result call() throws Exception {
			return this.checker.check(this.formula);
		}

	}

	private Map<String, Formula> formulas;
	private CTLModelChecker checker;

	public Manager(TransitionSystem pts, Map<String, Formula> formulas, LogicType type) {
		this.formulas = formulas;

		switch (type) {
		case CTL:
			this.checker = new CTLModelChecker(pts);
		}
	}

	public Map<String, Result> validateSequentially() {
		return this.formulas.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> this.checker.check(e.getValue())));
	}

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
