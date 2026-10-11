
package com.immverse.qa.reporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class CustomHtmlReporter implements IReporter {

    @Override
    public void generateReport(
            List<XmlSuite> xmlSuites,
            List<ISuite> suites,
            String outputDirectory) {

        int passed = 0;
        int failed = 0;
        int skipped = 0;

        StringBuilder rows = new StringBuilder();

        for (ISuite suite : suites) {
            for (ISuiteResult suiteResult :
                    suite.getResults().values()) {

                ITestContext context = suiteResult.getTestContext();

                passed += context.getPassedTests().size();
                failed += context.getFailedTests().size();
                skipped += context.getSkippedTests().size();

                appendRows(rows, context.getPassedTests().getAllResults(),
                        "PASSED");
                appendRows(rows, context.getFailedTests().getAllResults(),
                        "FAILED");
                appendRows(rows, context.getSkippedTests().getAllResults(),
                        "SKIPPED");
            }
        }

        File report = new File(
                "custom-report.html").getAbsoluteFile();

        try (PrintWriter out = new PrintWriter(
                new FileWriter(report, false))) {

            out.println("<!DOCTYPE html><html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>ParaBank QA Dashboard</title>");

            out.println("<style>");
            out.println("*{box-sizing:border-box}");
            out.println("body{margin:0;padding:30px;font-family:Arial,sans-serif;"
                    + "color:#fff;background:linear-gradient(135deg,#101827,#34243d)}");
            out.println(".container{max-width:1100px;margin:auto}");
            out.println("h1{color:#F48031}");
            out.println(".grid{display:grid;grid-template-columns:repeat(3,1fr);gap:18px}");
            out.println(".glass{padding:22px;margin:20px 0;border-radius:18px;"
                    + "background:rgba(255,255,255,.09);"
                    + "border:1px solid rgba(255,255,255,.2);"
                    + "backdrop-filter:blur(14px);"
                    + "box-shadow:0 8px 32px rgba(0,0,0,.2)}");
            out.println(".number{font-size:32px;font-weight:bold;margin-top:10px}");
            out.println(".label{color:#ccd2df}");
            out.println("table{width:100%;border-collapse:collapse}");
            out.println("th,td{text-align:left;padding:13px;border-bottom:1px solid #ffffff22}");
            out.println("th{color:#F48031}");
            out.println(".PASSED{color:#86efac}.FAILED{color:#fca5a5}.SKIPPED{color:#fcd34d}");
            out.println("pre{white-space:pre-wrap;overflow-wrap:anywhere;max-width:600px}");
            out.println("@media(max-width:650px){body{padding:12px}.grid{grid-template-columns:1fr}}");
            out.println("</style></head><body><div class='container'>");

            out.println("<h1>ParaBank QA Dashboard</h1>");
            out.println("<p>Generated from actual TestNG results</p>");

            out.println("<div class='grid'>");
            metric(out, "Passed", passed);
            metric(out, "Failed", failed);
            metric(out, "Skipped", skipped);
            out.println("</div>");

            out.println("<div class='glass'><h2>Test Execution Details</h2>");
            out.println("<p>Generated: "
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(new Date())
                    + "</p>");
            out.println("<table><thead><tr><th>Test</th>"
                    + "<th>Class</th><th>Status</th>"
                    + "<th>Details</th></tr></thead><tbody>");

            out.println(rows);
            out.println("</tbody></table></div>");

            out.println("<p class='label'>ParaBank QA Automation · "
                    + "Custom TestNG HTML Report</p>");
            out.println("</div></body></html>");

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not generate custom HTML report", e);
        }

        System.out.println(
                "Custom HTML report generated: " + report);
    }

    private void appendRows(
            StringBuilder rows,
            Iterable<ITestResult> results,
            String status) {

        for (ITestResult result : results) {
            String testName = escape(result.getName());
            String className = escape(
                    result.getTestClass().getName());

            String details = "";
            if (result.getThrowable() != null) {
                details = escape(
                        result.getThrowable().toString());
            }

            rows.append("<tr><td>")
                    .append(testName)
                    .append("</td><td>")
                    .append(className)
                    .append("</td><td class='")
                    .append(status)
                    .append("'>")
                    .append(status)
                    .append("</td><td><pre>")
                    .append(details)
                    .append("</pre></td></tr>");
        }
    }

    private void metric(
            PrintWriter out, String label, int value) {
        out.println("<div class='glass'><div class='label'>"
                + label + "</div><div class='number'>"
                + value + "</div></div>");
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
