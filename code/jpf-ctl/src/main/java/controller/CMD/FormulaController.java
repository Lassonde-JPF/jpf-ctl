package controller.CMD;

import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.ctl.CTLLexer;
import org.ctl.CTLParser;

import error.LabelChecker;
import error.LabelDoesNotExistException;
import formulas.Formula;
import formulas.Generator;

public class FormulaController {
	
	public static Formula parseFormula(Set<String> labels, String input) throws LabelDoesNotExistException {
		CharStream inputStream = CharStreams.fromString(input);
		ParseTree pT = new CTLParser(new CommonTokenStream(new CTLLexer(inputStream))).formula();
		
		LabelChecker.checkLabelsExist(labels, pT);
		
		return new Generator().visit(pT);
	}
	
}
