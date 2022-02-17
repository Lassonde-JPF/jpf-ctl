package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import formulas.Formula;
import labels.BinaryLabel;
import labels.Label;
import labels.UnaryLabel;
import model.Logic;

public class Logic {

	// Attributes
	private Map<String, Label> labels;
	private Map<String, Formula> formulas;
	
	// Type of Logic
	private LogicType type;
	
	public Logic(Map<String, Label> labels, Map<String, Formula> formulas, LogicType type) {
		this.labels = labels;
		this.formulas = formulas;
		this.type = type;
	}
	
	public Map<String, Label> getLabelMapping() {
		return this.labels;
	}

	public Map<String, Formula> getFormulaMapping() {
		return this.formulas;
	}

	public Collection<Label> getLabels() {
		return this.labels.values();
	}

	public Collection<Formula> getFormulas() {
		return this.formulas.values();
	}
	
	public LogicType getLogicType() {
		return this.type;
	}

	public Map<String, String> getJNIMapping() {
		Map<String, String> jniMapping = new HashMap<>();
		for (Entry<String, Label> e : this.labels.entrySet()) {
			String alias = e.getKey();
			Label label = e.getValue();
			if (label instanceof UnaryLabel) {
				jniMapping.put(alias, ((UnaryLabel) label).getName());
				continue;
			}
			if (label instanceof BinaryLabel) {
				jniMapping.put(alias, ((BinaryLabel) label).getJNIName());
				continue;
			}
		}
		return jniMapping;
	}
	
	public Map<String, String> getReverseJNIMapping() {
		Map<String, String> inverseMap = new HashMap<>();
		for (Entry<String, Label> e : this.labels.entrySet()) {
			String alias = e.getKey();
			Label label = e.getValue();
			if (label instanceof UnaryLabel) {
				inverseMap.put(((UnaryLabel) label).getName(), alias);
				continue;
			}
			if (label instanceof BinaryLabel) {
				inverseMap.put(((BinaryLabel) label).getJNIName(), alias);
				continue;
			}
		}
		return inverseMap;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object != null && this.getClass() == object.getClass()) {
			Logic other = (Logic) object;
			return this.formulas.equals(other.formulas) && this.labels.equals(other.labels);
		} else {
			return false;
		}
	}
}
