package com.assignment.api.specs;

import com.assignment.common.reporting.ExtentManager;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Logs every request/response to Log4j2 and to the current Extent test node. */
public class LoggingFilter implements Filter {

    private static final Logger LOG = LogManager.getLogger(LoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification req, FilterableResponseSpecification resSpec,
                           FilterContext ctx) {
        Response res = ctx.next(req, resSpec);

        String reqBody = req.getBody() == null ? "" : req.getBody().toString();
        String resBody = res.getBody().asPrettyString();
        LOG.info("REQUEST  {} {}\n{}", req.getMethod(), req.getURI(), reqBody);
        LOG.info("RESPONSE {} ({} ms)\n{}", res.getStatusCode(), res.getTime(), resBody);

        var test = ExtentManager.getTest();
        if (test != null) {
            test.info("<b>" + req.getMethod() + "</b> " + req.getURI());
            if (!reqBody.isBlank()) test.info(MarkupHelper.createCodeBlock(reqBody, CodeLanguage.JSON));
            test.info("Status: <b>" + res.getStatusCode() + "</b> in " + res.getTime() + " ms");
            test.info(MarkupHelper.createCodeBlock(resBody, CodeLanguage.JSON));
        }
        return res;
    }
}
