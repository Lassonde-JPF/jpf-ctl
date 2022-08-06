/*
 * Copyright (C)  2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jpf.logic.ctl;

import jpf.logic.ctl.CTLBaseVisitor;
import jpf.logic.ctl.CTLParser.AndContext;
import jpf.logic.ctl.CTLParser.AliasContext;
import jpf.logic.ctl.CTLParser.BracketContext;
import jpf.logic.ctl.CTLParser.ExistsAlwaysContext;
import jpf.logic.ctl.CTLParser.ExistsEventuallyContext;
import jpf.logic.ctl.CTLParser.ExistsNextContext;
import jpf.logic.ctl.CTLParser.ExistsUntilContext;
import jpf.logic.ctl.CTLParser.FalseContext;
import jpf.logic.ctl.CTLParser.ForAllAlwaysContext;
import jpf.logic.ctl.CTLParser.ForAllEventuallyContext;
import jpf.logic.ctl.CTLParser.ForAllNextContext;
import jpf.logic.ctl.CTLParser.ForAllUntilContext;
import jpf.logic.ctl.CTLParser.IffContext;
import jpf.logic.ctl.CTLParser.ImpliesContext;
import jpf.logic.ctl.CTLParser.NotContext;
import jpf.logic.ctl.CTLParser.OrContext;
import jpf.logic.ctl.CTLParser.TrueContext;

/**
 * Generates an abstract syntax tree from a parse tree.
 * 
 * @author Neena Govindhan
 * @author Jonas Laya
 * @author Jessie Leung
 * @author Paul Sison
 * @author Franck van Breugel
 * @author Parssa Khazra
 * @author Hongru Wang
 */
public class Visitor extends CTLBaseVisitor<CTLFormula> {

	/**
	 * Visits the given Bracket node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * Bracket node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Bracket
	 * @return A {@code Formula} instance that represents abstract syntax tree
	 *         corresponding to the formula within the brackets
	 */
	@Override
	public CTLFormula visitBracket(BracketContext context) {
		return visit(context.formula());
	}

	/**
	 * Visits the given ForAllAlways node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * ForAllAlways node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllAlways
	 *                alternative
	 * @return A {@code ForAllAlways} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public CTLFormula visitForAllAlways(ForAllAlwaysContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ForAllAlways(formula);
	}

	/**
	 * Visits the left and right sub trees of the given Or node in the parse tree
	 * and returns an Or instance containing the left and right abstract syntax
	 * trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the Or
	 * @return A {@code Or} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitOr(OrContext context) {
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		return new Or(left, right);
	}

	/**
	 * Visits the left and right subtrees of the given Iff node in the parse tree
	 * and returns an Iff object containing the abstract syntax trees corresponding
	 * to the left and right subtrees of the parse tree rooted at the Iff node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Iff
	 *                alternative
	 * @return A {@code Iff} instance that represents the abstract syntax tree
	 *         corresponding to the left and right subtrees of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitIff(IffContext context) {
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		return new Iff(left, right);
	}

	/**
	 * Visits the given True Terminal node in the parse tree
	 * 
	 * @param context a node in the syntax tree that corresponds to the True
	 *                alternative
	 * @return A {@code True} instance
	 */
	@Override
	public CTLFormula visitTrue(TrueContext context) {
		return new True();
	}

	/**
	 * Visits the given False Terminal node in the parse tree
	 * 
	 * @param context a node in the syntax tree that corresponds to the False
	 *                alternative
	 * @return A {@code False} instance
	 */
	@Override
	public CTLFormula visitFalse(FalseContext context) {
		return new False();
	}

	/**
	 * Visits the given ExistsEventually node in the parse tree and returns the
	 * abstract syntax tree corresponding to the subtree of the parse tree rooted at
	 * the ExistsEventually node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                ExistsEventually alternative
	 * @return A {@code ExistsEventually} instance that represents abstract syntax
	 *         tree corresponding to the subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitExistsEventually(ExistsEventuallyContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ExistsEventually(formula);
	}

	/**
	 * Visits the given AtomicProposition Terminal node in the parse tree and return
	 * the context of atomic proposition
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                AtomicProposition alternative
	 * @return A {@code AtomicProposition} instance containing a string
	 *         representation of an atomic proposition as defined by the grammar
	 */
	@Override
	public CTLFormula visitAlias(AliasContext context) {
		return new Alias(context.ALIAS().getText());
	}

	/**
	 * Visits the given ForAllEventually node in the parse tree and returns the
	 * abstract syntax tree corresponding to the subtree of the parse tree rooted at
	 * the ForAllEventually node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                ForAllEventually alternative
	 * @return A {@code ForAllEventually} instance that represents abstract syntax
	 *         tree corresponding to the subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitForAllEventually(ForAllEventuallyContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ForAllEventually(formula);
	}

	/**
	 * Visits the given Not node in the parse tree and returns the abstract syntax
	 * tree corresponding to the subtree of the parse tree rooted at the Not Node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Not
	 *                alternative
	 * @return A {@code Not} instance that represents the abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public CTLFormula visitNot(NotContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new Not(formula);
	}

	/**
	 * Visits the left and right sub trees of the given ForAllUntil node in the
	 * parse tree and returns an ForAllUntil instance containing the left and right
	 * abstract syntax trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllUntil
	 *                formula
	 * @return A {@code ForAllUntil} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitForAllUntil(ForAllUntilContext context) {
		// AU is right associative so we visit the right sub tree first
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		return new ForAllUntil(left, right);
	}

	/**
	 * Visits the left and right sub trees of the given Implies node in the parse
	 * tree and returns an Implies instance containing the left and right abstract
	 * syntax trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the Implies
	 *                formula
	 * @return A {@code Implies} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitImplies(ImpliesContext context) {
		// -> is right associative so we visit the right sub tree first
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		return new Implies(left, right);
	}

	/**
	 * Visits the given ForAllNext node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * ForAllNext node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ForAllNext
	 *                alternative
	 * @return A {@code ForAllNext} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public CTLFormula visitForAllNext(ForAllNextContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ForAllNext(formula);
	}

	/**
	 * Visits the left and right sub trees of the given Implies node in the parse
	 * tree and returns an And instance containing the left and right abstract
	 * syntax trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the And formula
	 * @return A {@code And} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitAnd(AndContext context) {
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		return new And(left, right);
	}

	/**
	 * Visits the given ExistsAlways node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * ExistsAlways node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistsAlways
	 *                alternative
	 * @return A {@code ExistsAlways} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public CTLFormula visitExistsAlways(ExistsAlwaysContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ExistsAlways(formula);
	}

	/**
	 * Visits the left and right sub trees of the given ExistsUntil node in the
	 * parse tree and returns an ExistsUntil instance containing the left and right
	 * abstract syntax trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistUntil
	 *                formula
	 * @return A {@code ExistsUntil} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public CTLFormula visitExistsUntil(ExistsUntilContext context) {
		// EU is right associative so we visit the right sub tree first
		CTLFormula right = (CTLFormula) visit(context.formula(1));
		CTLFormula left = (CTLFormula) visit(context.formula(0));
		return new ExistsUntil(left, right);
	}

	/**
	 * Visits the given ExistsNext node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * ExistsNext node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the ExistsNext
	 *                alternative
	 * @return A {@code ExistsNext} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public CTLFormula visitExistsNext(ExistsNextContext context) {
		CTLFormula formula = (CTLFormula) visit(context.formula());
		return new ExistsNext(formula);
	}
}