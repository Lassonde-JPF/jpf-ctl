package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import formulas.Formula;
import labels.Label;
import model.Target;

public class CTLParser {

	//Static filename
	private static final String FILE_NAME = "ctl.properties";

	// Attributes
	private Map<String, Label> labels;
	private Map<String, Formula> formulae;

	// Default Contructor
	public CTLParser(String path, Target target) throws IOException {
		String filePath = (path == null) ? Paths.get(".").toAbsolutePath().normalize().toString() : path;
		filePath += File.separator + FILE_NAME;
		
		// Parse Labels First
		this.labels = new HashMap<String, Label>();
		Files.lines(Paths.get(filePath)).filter(line -> line.contains(":")).forEach(line -> {
			String alias = line.substring(0, line.indexOf(":")).trim();
			String label = line.substring(line.indexOf(":") + 1).trim();

			this.labels.computeIfAbsent(alias, k -> LabelParser.parseLabel(target, label));
		});

		// Parse Formulas Second
		this.formulae = new HashMap<String, Formula>();
		Files.lines(Paths.get(filePath)).filter(line -> line.contains("=")).forEach(line -> {
			String alias = line.substring(0, line.indexOf("=")).trim();
			String formula = line.substring(line.indexOf("=") + 1).trim();

			this.formulae.computeIfAbsent(alias, k -> FormulaParser.parseFormula(this.labels.keySet(), formula));
		});
	}

	public Map<String, Label> getLabelMapping() {
		return this.labels;
	}
	
	public Map<String, Formula> getFormulaMapping() {
		return this.formulae;
	}
	
	public Collection<Label> getLabels() {
		return this.labels.values();
	}

	public Collection<Formula> getFormulas() {
		return this.formulae.values();
	}

	// TODO perhaps change to ".JNIName"
	public Map<String, String> getJNIMapping() {
		return this.labels.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
	}
}
