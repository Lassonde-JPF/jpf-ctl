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

package jpf.logic.ltl;

import jpf.logic.ltl.LTLBaseVisitor;
import jpf.logic.ltl.LTLParser.AndContext;
import jpf.logic.ltl.LTLParser.AliasContext;
import jpf.logic.ltl.LTLParser.AlwaysContext;
import jpf.logic.ltl.LTLParser.BracketContext;
import jpf.logic.ltl.LTLParser.EventuallyContext;
import jpf.logic.ltl.LTLParser.FalseContext;
import jpf.logic.ltl.LTLParser.IffContext;
import jpf.logic.ltl.LTLParser.ImpliesContext;
import jpf.logic.ltl.LTLParser.NextContext;
import jpf.logic.ltl.LTLParser.NotContext;
import jpf.logic.ltl.LTLParser.OrContext;
import jpf.logic.ltl.LTLParser.TrueContext;
import jpf.logic.ltl.LTLParser.UntilContext;

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
public class Visitor extends LTLBaseVisitor<Formula> {

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
	public Formula visitBracket(BracketContext context) {
		return visit(context.formula());
	}

	/**
	 * Visits the given Always node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * Always node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Always
	 *                alternative
	 * @return A {@code Always} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitAlways(AlwaysContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Always(formula);
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
	public Formula visitOr(OrContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
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
	public Formula visitIff(IffContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
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
	public Formula visitTrue(TrueContext context) {
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
	public Formula visitFalse(FalseContext context) {
		return new False();
	}

	/**
	 * Visits the given Eventually node in the parse tree and returns the
	 * abstract syntax tree corresponding to the subtree of the parse tree rooted at
	 * the Eventually node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                Eventually alternative
	 * @return A {@code Eventually} instance that represents abstract syntax
	 *         tree corresponding to the subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public Formula visitEventually(EventuallyContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Eventually(formula);
	}

	/**
	 * Visits the given Alias Terminal node in the parse tree and return
	 * the context of the alias
	 * 
	 * @param context a node in the syntax tree that corresponds to the
	 *                Alias alternative
	 * @return A {@code Alias} instance containing a string
	 *         representation of an alias as defined by the grammar
	 */
	@Override
	public Formula visitAlias(AliasContext context) {
		return new Alias(context.ALIAS().getText());
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
	public Formula visitNot(NotContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Not(formula);
	}

	/**
	 * Visits the left and right sub trees of the given Until node in the
	 * parse tree and returns an Until instance containing the left and right
	 * abstract syntax trees
	 * 
	 * @param context a node in the syntax tree that corresponds to the Until
	 *                formula
	 * @return A {@code Until} instance that represents abstract syntax tree
	 *         corresponding to the left and right subtree of the parse tree rooted
	 *         {@code context}
	 */
	@Override
	public Formula visitUntil(UntilContext context) {
		// AU is right associative so we visit the right sub tree first
		Formula right = (Formula) visit(context.formula(1));
		Formula left = (Formula) visit(context.formula(0));
		return new Until(left, right);
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
	public Formula visitImplies(ImpliesContext context) {
		// -> is right associative so we visit the right sub tree first
		Formula right = (Formula) visit(context.formula(1));
		Formula left = (Formula) visit(context.formula(0));
		return new Implies(left, right);
	}

	/**
	 * Visits the given Next node in the parse tree and returns the abstract
	 * syntax tree corresponding to the subtree of the parse tree rooted at the
	 * Next node.
	 * 
	 * @param context a node in the syntax tree that corresponds to the Next
	 *                alternative
	 * @return A {@code Next} instance that represents abstract syntax tree
	 *         corresponding to the subtree of the parse tree rooted {@code context}
	 */
	@Override
	public Formula visitNext(NextContext context) {
		Formula formula = (Formula) visit(context.formula());
		return new Next(formula);
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
	public Formula visitAnd(AndContext context) {
		Formula left = (Formula) visit(context.formula(0));
		Formula right = (Formula) visit(context.formula(1));
		return new And(left, right);
	}
}