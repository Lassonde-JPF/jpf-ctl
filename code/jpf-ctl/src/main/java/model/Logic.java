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

/**
 * Logic - Represents a logic language for use in model checking
 * 
 * @author mattw
 * @author Franck van Breugel
 */
public class Logic {

	// Attributes
	private Map<String, Label> labels;
	private Map<String, Formula> formulas;

	// Type of Logic
	private LogicType type;

	/**
	 * Initializes this Logic object with a given map of labels, formulas and a type
	 * of logic.
	 * 
	 * @param labels   - a map of alias' -> labels
	 * @param formulas - a map of alias' -> formulas
	 * @param type     - the type of logic to apply
	 */
	public Logic(Map<String, Label> labels, Map<String, Formula> formulas, LogicType type) {
		this.labels = labels;
		this.formulas = formulas;
		this.type = type;
	}

	/**
	 * standard getter for this logic's label mapping
	 * 
	 * @return the map of alias' -> labels for this logic
	 */
	public Map<String, Label> getLabelMapping() {
		return this.labels;
	}

	/**
	 * standard getter for this logic's formula mapping
	 * 
	 * @return the map of alias' -> formulas for this logic
	 */
	public Map<String, Formula> getFormulaMapping() {
		return this.formulas;
	}

	/**
	 * standard getter for this logic's labels
	 * 
	 * @return the collection of label objects for this logic
	 */
	public Collection<Label> getLabels() {
		return this.labels.values();
	}

	/**
	 * standard getter for this logic's formulas
	 * 
	 * @return the collection of formula objects for this logic
	 */
	public Collection<Formula> getFormulas() {
		return this.formulas.values();
	}

	/**
	 * standard getter for this logic's type
	 * 
	 * @return the logic type associated with this logic
	 */
	public LogicType getLogicType() {
		return this.type;
	}

	/**
	 * Build and return the Java Native Interface mapping
	 * 
	 * @return map representing alias' to JNI names
	 */
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

	/**
	 * Build and return the inverse Java Native Interface mapping
	 * 
	 * @return map representing JNI names to alias'
	 */
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
