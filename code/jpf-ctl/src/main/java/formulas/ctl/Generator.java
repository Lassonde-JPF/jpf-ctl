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

package formulas.ctl;

import org.ctl.CTLBaseVisitor;
import org.ctl.CTLParser.AndContext;
import org.ctl.CTLParser.AtomicPropositionContext;
import org.ctl.CTLParser.BracketContext;
import org.ctl.CTLParser.ExistsAlwaysContext;
import org.ctl.CTLParser.ExistsEventuallyContext;
import org.ctl.CTLParser.ExistsNextContext;
import org.ctl.CTLParser.ExistsUntilContext;
import org.ctl.CTLParser.FalseContext;
import org.ctl.CTLParser.ForAllAlwaysContext;
import org.ctl.CTLParser.ForAllEventuallyContext;
import org.ctl.CTLParser.ForAllNextContext;
import org.ctl.CTLParser.ForAllUntilContext;
import org.ctl.CTLParser.IffContext;
import org.ctl.CTLParser.ImpliesContext;
import org.ctl.CTLParser.NotContext;
import org.ctl.CTLParser.OrContext;
import org.ctl.CTLParser.TrueContext;

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
public class Generator extends CTLBaseVisitor<CTLFormula> {

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
	 * Visits the given AtomicProposition Terminal node in the parse tree and return
	 * the context of atomic proposition
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                AtomicProposition alternative
	 * @return A {@code AtomicProposition} instance containing a string
	 *         representation of an atomic proposition as defined by the grammar
	 */
	@Override
	public CTLFormula visitAtomicProposition(AtomicPropositionContext context) {
		return new AtomicProposition(context.ATOMIC_PROPOSITION().getText());
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
}