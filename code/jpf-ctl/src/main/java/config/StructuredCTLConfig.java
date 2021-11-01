package config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;


import ctl.Formula;
import ctl.Generator;
import label.Label;
import label.Type;
import logging.Logger;

public class StructuredCTLConfig {
	
	// Config Attributes
	Map<String, Label> labels;
	List<Formula> formulae;
	Set<Type> types;
	Target target;
	String targetArgs, enumerateRandom;
	
	// Regex 
	String ALIAS = "[a-zA-Z_][a-zA-Z0-9_]*:\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";
	String FORMULA = "[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*[a-zA-Z_(!][a-zA-Z0-9_()!\\s]*";
	
	// Logging
	Logger logger;
	
	public StructuredCTLConfig(File configFile, File targetFile, String targetArgs, String enumerateRandom) throws IOException, ClassNotFoundException {
		// New logger
		logger = new Logger(StructuredCTLConfig.class.getName(), "StructuredCTLConfig");
		
		// Path to actual .ctl file
		Path pathToFile = Paths.get(configFile.getPath());
		
		// Compile into pattern as we will be checking multiple lines (matches)
		Pattern ALIAS_PAT = Pattern.compile(ALIAS);
		Pattern FORMULA_PAT = Pattern.compile(FORMULA);
		
		// Initialize attributes
		labels = new HashMap<String, Label>();
		formulae = new ArrayList<Formula>();
		types = new HashSet<Type>();

		Files.lines(pathToFile).map(String::trim).forEach(line -> {
			if (ALIAS_PAT.matcher(line).matches()) {
				String alias = line.split(":")[0].trim();
				String fields = line.split(":")[1].trim();
				Type type = Type.valueOf(fields.split("\\s")[0]);
				String qualifiedName = fields.split("\\s")[1];
				
				boolean valid = Type.validate(type, qualifiedName);
				if (!valid) {
					logger.severe("Unable to validate atomic proposition " + alias);
				}

				this.types.add(type);
				this.labels.computeIfAbsent(alias, k -> new Label(type, qualifiedName));
			}
			if (FORMULA_PAT.matcher(line).matches()) {
				//String alias = line.split("=")[0].trim(); // TODO consider removing alias for formula
				String formula = line.split("=")[1].trim();
				
				CharStream input = CharStreams.fromString(formula);
				
				ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(input))).formula();
				
				Formula f = new Generator().visit(pT);
				
				this.formulae.add(f);
			}
		});
		logger.info("\n\tAtomic Propositions Defined:\n\t\t" + this.labels.toString() + "\n\tFormulae Defined:\n\t\t" + this.formulae.toString());
	
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
		
		this.targetArgs = targetArgs;
		this.enumerateRandom = enumerateRandom;
	}
	
	public Set<Type> getUniqueTypes() { // TODO rename -> only returns types that have label defs
		return this.types.stream()
				.filter(type -> !type.equals(Type.Initial))
				.filter(type -> !type.equals(Type.End))
				.collect(Collectors.toSet());
	}
	
	public String getLabelClasses() {
		return this.types.stream()
				.map(t -> "label." + t.toString())
				.collect(Collectors.joining("; "));
	}
	
	public String getLabelsOfType(Type t) {
		return this.labels.values().stream()
				.filter(v -> v.getType().equals(t))
				.map(Label::getQualifiedName)
				.collect(Collectors.joining("; "));
	}
	
	public List<Formula> getFormulae() {
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
	
}
