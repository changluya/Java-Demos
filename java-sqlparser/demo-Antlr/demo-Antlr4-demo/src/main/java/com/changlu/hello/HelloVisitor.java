// Generated from Hello.g4 by ANTLR 4.8

package com.changlu.hello;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link HelloParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface HelloVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link HelloParser#s}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitS(HelloParser.SContext ctx);
}