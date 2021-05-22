package ctl;

import org.antlr.v4.runtime.ParserRuleContext;

import parser.CTLBaseListener;

public class CTLPrinter extends CTLBaseListener {
    @Override public void enterEveryRule(ParserRuleContext ctx) {  //see gramBaseListener for allowed functions
        System.out.println("rule entered: " + ctx.getText());      //code that executes per rule
    }
}
