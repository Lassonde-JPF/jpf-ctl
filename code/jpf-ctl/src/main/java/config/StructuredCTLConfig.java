package config;

import java.io.IOException;
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

import java.util.regex.Matcher;

import ctl.AtomicProposition;
import ctl.Formula;
import ctl.Generator;
import error.CTLError;
import label.Label;
import label.Type;
import logging.Logger;

public class StructuredCTLConfig {
	
	// Config Attributes
	Map<String, Label> labels;
	List<Formula> formulae;
	Set<Type> types;
	
	// Regex 
	String ALIAS = "[a-zA-Z_][a-zA-Z0-9_]*:\\s*[a-zA-Z_][a-zA-Z0-9_]*\\s*([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*";
	String FORMULA = "[a-zA-Z_][a-zA-Z0-9_]*\\s*=\\s*[a-zA-Z_(!][a-zA-Z0-9_()!\\s]*";
	
	// Logging
	Logger logger;
	
	public StructuredCTLConfig(String filePath) throws IOException {
		// New logger
		logger = new Logger(StructuredCTLConfig.class.getName(), "StructuredCTLConfig");
		
		// Path to actual .ctl file
		Path pathToFile = Paths.get(filePath);
		
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
				
				// TODO implement - for now assume true
				switch (type) {
					case Initial:
					case End:
					case BooleanStaticField:
					case IntegerStaticField:
					case BooleanLocalVariable:
					case IntegerLocalVariable:
					case InvokedMethod:
					case ReturnedBooleanMethod:
					case ReturnedIntegerMethod:
					case ReturnedVoidMethod:
					case SynchronizedStaticMethod:
					case ThrownException:
				}

				this.types.add(type);
				this.labels.computeIfAbsent(alias, k -> new Label(type, qualifiedName));
			}
			if (FORMULA_PAT.matcher(line).matches()) {
				String alias = line.split("=")[0].trim();
				String formula = line.split("=")[1].trim();
				
				CharStream input = CharStreams.fromString(formula);
				
				ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(input))).formula();
				
				Formula f = new Generator().visit(pT);
				
				this.formulae.add(f);
			}
		});
		logger.info("Atomic Propositions Defined:\n" + this.labels.toString() + "\nFormulae Defined:\n" + this.formulae.toString());
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
	
	public Label getLabel(String alias) {
		return labels.get(alias);
	}
	
}
