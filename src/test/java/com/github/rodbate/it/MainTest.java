package com.github.rodbate.it;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;


import static org.junit.Assert.*;


public class MainTest {


    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(new LoggingRule("OUTER RULE"))
            .around(new LoggingRule("MIDDLE RULE"))
            .around(new LoggingRule("INNER RULE"));


    @Test
    public void testM1(){



    }






    static class LoggingRule implements TestRule {

        private final String msg;

        public LoggingRule(String msg) {
            this.msg = msg;
        }
        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    System.out.println("Starting : " + msg);
                    try {
                        base.evaluate();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        System.out.println("Finishing : " + msg);
                    }
                }
            };
        }
    }



}
