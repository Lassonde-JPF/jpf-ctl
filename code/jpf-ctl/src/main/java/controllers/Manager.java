package controllers;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import formulas.Formula;
import model.ModelChecker;

public class Manager {

	private Map<String, Formula> formulas;
	private ModelChecker checker;

	public Manager(TransitionSystem pts, Map<String, String> jniMapping, Map<String, Formula> formulas) {
		this.formulas = formulas;
		this.checker = new ModelChecker(pts, jniMapping);
	}
	
	public Map<String, ModelChecker.Result> validateSequentially() {
		return this.formulas.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> this.checker.check(e.getValue())));
	}
	
	public Collection<ModelChecker.Result> validateParallel() {
		return null; // TODO implementation
	}
	
}
