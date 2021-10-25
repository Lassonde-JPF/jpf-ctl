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

package ctl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.tree.ParseTree;

import org.junit.jupiter.api.Test;

/**
 * Tests a range of atomic propositions.
 * 
 * @author Jinho Hwang
 * @author Minh Cu (Huy Cu)
 * @author Alberto Mastrofrancesco
 * @author Qasim Ahmed
 */
public class AtomicPropositionTest extends BaseTest {
	
//	/**
//	 * Tests an atomic proposition consisting of Korean characters.
//	 */
//	@Test
//	public void testKoreanName() {
//		String atomicProposition = "ìž�.ë°”.í�´.ëž˜.ìŠ¤";
//		Formula expected = new AtomicProposition(atomicProposition);
//		ParseTree tree = parse(atomicProposition);
//		Formula actual = this.generator.visit(tree);
//		assertNotNull(actual);
//		assertEquals(expected, actual);
//	}
	
//	/**
//	 * Tests an atomic proposition consisting of Vietnamese characters.
//	 */
//	@Test
//	public void testVietnameseName() {
//		String atomicProposition = "cá»¥cCá»©t.cá»¥cCá»©t";
//		Formula expected = new AtomicProposition(atomicProposition);
//		ParseTree tree = parse(atomicProposition);
//		Formula actual = this.generator.visit(tree);
//		assertNotNull(actual);
//		assertEquals(expected, actual);
//	}
	
//	/**
//	 * Tests an atomic proposition that starts with a number.
//	 */
//	@Test
//	public void testNumberFirst() {
//		String atomicProposition = "1java.awt";
//		Formula expected = new AtomicProposition(atomicProposition);
//		ParseTree tree = parse(atomicProposition);
//		Formula actual = this.generator.visit(tree);
//		assertNotNull(actual);
//		assertEquals(expected, actual);
//	}
	
//	/**
//	 * Tests an atomic proposition that starts with a special character.
//	 */
//	@Test
//	public void testSpecialCharacterFirst() {
//		String atomicProposition = "#java.awt";
//		Formula expected = new AtomicProposition(atomicProposition);
//		ParseTree tree = parse(atomicProposition);
//		Formula actual = this.generator.visit(tree);
//		assertNotNull(actual);
//		assertEquals(expected, actual);
//	}
	
	/**
	 * Tests a very long atomic proposition.
	 */
	@Test
	public void testVeryLongPackageNames() {
		String atomicProposition = "java.a.b.c.d.e.f.g.h.i.j.k.l.m.n.o.p.q.r.s.t.u.v.w.x.y.z";
		Formula expected = new AtomicProposition(atomicProposition);
		ParseTree tree = parse(atomicProposition);
		Formula actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
	
	/**
	 * Tests an atomic proposition that is the name of a class.
	 */
	@Test
	public void testJDKName() {
		String atomicProposition = "java.lang.Object";
		Formula expected = new AtomicProposition(atomicProposition);
		ParseTree tree = parse(atomicProposition);
		Formula actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.AbstractCollection";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.AbstractList";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.ArrayList";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.AbstractQueue";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.concurrent.PriorityBlockingQueue";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.util.concurrent.LinkedBlockingDeque";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);

		atomicProposition = "java.security.spec.ECPoint";
		expected = new AtomicProposition(atomicProposition);
		tree = parse(atomicProposition);
		actual = this.generator.visit(tree);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
