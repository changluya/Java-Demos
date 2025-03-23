package com.changlu.demo.parse;

import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.util.validation.Validation;
import net.sf.jsqlparser.util.validation.ValidationError;
import net.sf.jsqlparser.util.validation.feature.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * StatementValidator验证器
 */
public class demo09 {

    public static final Logger log = LoggerFactory.getLogger(demo09.class);

    public static void main(String[] args) throws Exception{
        String sql = "SELECT * FROM myview v JOIN secondview v2 ON v.id = v2.ref";
// 满足条件指明支持SELECT
        Validation validation = new Validation(Arrays.asList(FeaturesAllowed.SELECT), sql);
        List<ValidationError> errors = validation.validate();
// no errors, select - statement is allowed
        if (errors.isEmpty()) {
            // do something else with the parsed statements
            Statements statements = validation.getParsedStatements();
            System.out.println(statements);
        }
        FeaturesAllowed exec = new FeaturesAllowed("EXECUTE", Feature.execute).unmodifyable();
    }

}
