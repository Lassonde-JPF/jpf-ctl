package controller.CMD;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import controller.FormulaController;
import controller.LabelController;
import formulas.Formula;
import labels.Label;
import logging.Logger;
import model.Logic;
import model.LogicType;

public class LogicController {

	// Static filename
	private static final String FILE_NAME = "logic.properties";

	// Default Contructor
	public static Logic parseLogic(String path, String classpath, LogicType logic) throws IOException, IllegalArgumentException {
		// Initialize Logging
		Logger logger = new Logger(LogicController.class.getSimpleName());

		// Parse the path
		String filePath = (path == null) ? Paths.get(".").toAbsolutePath().normalize().toString() : path;
		filePath += File.separator + FILE_NAME;
		logger.info("Parsed path: " + filePath);
		
		// Parse Labels First
		Map<String, Label> labels = new HashMap<String, Label>();
		Files.lines(Paths.get(filePath)).filter(line -> line.contains(":")).forEach(line -> {
			String alias = line.substring(0, line.indexOf(":")).trim();
			String label = line.substring(line.indexOf(":") + 1).trim();

			labels.computeIfAbsent(alias, k -> LabelController.parseLabel(classpath, label));
			logger.info("Parsed Label: " + alias + " = " + labels.get(alias));
		});

		// Parse Formulas Second
		Map<String, Formula> formulas = new HashMap<String, Formula>();
		Files.lines(Paths.get(filePath)).filter(line -> line.contains("=")).forEach(line -> {
			String alias = line.substring(0, line.indexOf("=")).trim();
			String formula = line.substring(line.indexOf("=") + 1).trim();

			formulas.computeIfAbsent(alias, k -> FormulaController.parseFormula(labels.keySet(), formula, logic));
			logger.info("Parsed Formula: " + alias + " = " + formulas.get(alias));
		});
		
		return new Logic(labels, formulas, logic);
	}


}
