package config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;
import org.label.LabelLexer;
import org.label.LabelParser;

import ctl.Formula;
import ctl.Generator;
import logging.Logger;

import label.Label;

public class StructuredCTLConfig {
	
	// Config Attributes
	Map<String, Label> labels;
	Map<String, Formula> formulae;

	Target target;
	String targetArgs, enumerateRandom;
	
	// Logging
	Logger logger;
	
	public StructuredCTLConfig(File configFile, File targetFile, String targetArgs, String enumerateRandom) throws IOException, ClassNotFoundException {
		// New logger
		logger = new Logger(StructuredCTLConfig.class.getName(), "StructuredCTLConfig");
		
		// Path to actual .ctl file
		Path pathToFile = Paths.get(configFile.getPath());
		
		// Initialize attributes
		formulae = new HashMap<String, Formula>();
		labels = new HashMap<String, Label>();

		// Build formulae and atomic proposition labels
		Files.lines(pathToFile).map(String::trim).forEach(line -> {
			// Atomic Proposition
			if (line.contains(":")) {
				String alias = line.split(":\\s")[0].trim();
				String fields = line.substring(line.indexOf(":")+1).trim();
				
				CharStream input = CharStreams.fromString(fields);
				
				ParseTree pT = new LabelParser(new CommonTokenStream(new LabelLexer(input))).label();
				
				Label l = new label.Generator().visit(pT);

				this.labels.computeIfAbsent(alias, k -> l);
			}
			// Formula
			if (line.contains("=")) {
				String alias = line.split("=")[0].trim(); 
				String formula = line.split("=")[1].trim();
				
				CharStream input = CharStreams.fromString(formula);
				
				ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(input))).formula();
				
				Formula f = new Generator().visit(pT);
				
				this.formulae.computeIfAbsent(alias, k -> f);
			}
		});

		// Build Target Object
		String className, packageName, path;
		URL[] url;
		try {
			className = targetFile.getName().split("\\.")[0];
			path = targetFile.getParentFile().getPath();
			url = new URL[] {
					targetFile.getParentFile().toURI().toURL()
			};
			Class.forName(className, false, new URLClassLoader(url));
			this.target = new Target(className, path);
		} catch (NoClassDefFoundError | ClassNotFoundException e) {
			packageName = targetFile.getParentFile().getName();
			className = packageName + "." + targetFile.getName().split("\\.")[0];
			path = targetFile.getParentFile().getParentFile().getPath();
			url = new URL[] {
					targetFile.getParentFile().getParentFile().toURI().toURL()
			};
			Class.forName(className, false, new URLClassLoader(url));
			this.target = new Target(className, packageName, path);
		}
		
		// Build additional info
		this.targetArgs = targetArgs;
		this.enumerateRandom = enumerateRandom;
	}
	
	public String getLabelClasses() {
		return this.labels.values().stream()
				.map(Label::classDef)
				.distinct()
				.collect(Collectors.joining(";"));
	}
	
	public Map<String, Label> getLabels() {
		return this.labels;
	}
	
	
	public Map<String, Formula> getFormulae() {
		return this.formulae;
	}
	
	public Target getTarget() {
		return this.target;
	}
	
	public String getTargetArgs() {
		return this.targetArgs;
	}
	
	public String getEnumerateRandom() {
		return this.enumerateRandom;
	}
	
	@Override
	public String toString() {
		String tmp = "";
		for (Entry<String, Label> e : this.labels.entrySet()) {
			tmp += e.getKey() + ": " + e.getValue() + "\n"; 
		}
		for (Entry<String, Formula> e : this.formulae.entrySet()) {
			tmp += e.getKey() + "=" + e.getValue() + "\n";
		}
		tmp += this.target + "\n";
		tmp += "Target Arguments: " + this.targetArgs + "\n";
		tmp += "EnumerateRandom: " + this.enumerateRandom;
		return tmp;
	}
	
}