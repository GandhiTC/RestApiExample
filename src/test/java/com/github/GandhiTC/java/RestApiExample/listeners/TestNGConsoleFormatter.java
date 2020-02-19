package com.github.GandhiTC.java.RestApiExample.listeners;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.testng.IAlterSuiteListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;



public class TestNGConsoleFormatter implements ITestListener, ISuiteListener, IAlterSuiteListener
{
	private Date suiteStartDate;




	@Override
	public void onTestStart(ITestResult result)
	{
		String 	testMethodName	= result.getName();
		String 	headerText		= "\t" + testMethodName + "\r\n\t";

		//	Use switch when there are more than 5 cases, otherwise use if/else if.
		switch(testMethodName)
		{
			case "testCreateEmployee":
				headerText += "Create a new employee";
				break;

			case "testNegativeCreateEmployee":
				headerText += "Make sure a new employee is not created";
				break;

			case "testGetSingleEmployee":
				headerText += "Retrieve single employee data";
				break;

			case "testUpdateEmployee":
				headerText += "Update an employee";
				break;

			case "testVerifyUpdates":
				headerText += "Verify employee properly updated";
				break;

			case "testDeleteEmployee":
				headerText += "Delete an employee";
				break;

			case "testGetAllEmployees":
				headerText += "Retrieve data for all employees";
				break;

			case "testDeleteAllEmployees":
				headerText += "Cleanup employee data";
				break;

			default:
				headerText += "Unknown Test";
				break;
		}

		System.out.println("-----------------------------------------------");
		System.out.println("START");
		System.out.println("-----------------------------------------------");
		System.out.println(headerText);
		System.out.println("-----------------------------------------------\r\n\r\n");
	}


	@Override
	public void onTestSuccess(ITestResult result)
	{
		String testMethodName	= result.getName();

		System.out.println("\r\n\r\n-----------------------------------------------");
		System.out.println("\t" + testMethodName);
		System.err.println("\tTEST COMPLETED SUCCESSFULLY!");
		System.out.println("-----------------------------------------------");
		System.out.println("END");
		System.out.println("-----------------------------------------------\r\n\r\n\r\n\r\n");
	}


	@Override
	public void onTestFailure(ITestResult result)
	{
		String testMethodName	= result.getName();

		System.out.println("\r\n\r\n-----------------------------------------------");
		System.out.println("\t" + testMethodName);
		System.err.println("\tTEST FAILED!");
		System.out.println("-----------------------------------------------");
		System.out.println("END");
		System.out.println("-----------------------------------------------\r\n\r\n\r\n\r\n");
	}


	@Override
	public void onTestSkipped(ITestResult result)
	{
		String testMethodName	= result.getName();

		System.out.println("\r\n\r\n-----------------------------------------------");
		System.out.println("\t" + testMethodName);
		System.err.println("\tTEST SKIPPED!");
		System.out.println("-----------------------------------------------");
		System.out.println("END");
		System.out.println("-----------------------------------------------\r\n\r\n\r\n\r\n");
	}


	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
		// TODO Auto-generated method stub
	}




	@Override
	public void onStart(ITestContext context)
	{
		String	testName	= context.getName().replace("/", "\r\n\t\t ");

		System.out.println("==============================================================================================");
		System.out.println("==============================================================================================");
		System.out.println("\t" + testName);
		System.out.println("==============================================================================================");
		System.out.println("==============================================================================================\r\n\r\n");
	}


	@Override
	public void onFinish(ITestContext context)
	{
		String	testName		= context.getName().replace("/", "\r\n\t\t ");
		double 	passedTests		= context.getPassedTests().getAllResults().size();
		double 	failedTests		= context.getFailedTests().getAllResults().size();
		double 	skippedTests	= context.getSkippedTests().getAllResults().size();
		double	totalTests		= passedTests + failedTests + skippedTests;
		double	passPercent		= (passedTests  * 100.00) / totalTests;
		double	failPercent		= (failedTests  * 100.00) / totalTests;
		double	skipPercent		= (skippedTests * 100.00) / totalTests;
		String	testDuration	= DurationFormatUtils.formatDuration(context.getEndDate().getTime() - context.getStartDate().getTime(), "HH 'hours' mm 'minutes' ss 'seconds'");

		System.out.println("\r\n\r\n==============================================================================================");
		System.out.println("==============================================================================================");
		System.out.println("\t" + testName + "\r\n");
		System.out.println("\t      Duration : " + testDuration + "\r\n");
		System.out.println("\tTotal Tests    : " + (int)totalTests);
		System.out.println("\tTotal Passed   : " + (int)passedTests  + " (" + String.format("%05.2f", passPercent) + "%)");
		System.out.println("\tTotal Failed   : " + (int)failedTests  + " (" + String.format("%05.2f", failPercent) + "%)");
		System.out.println("\tTotal Skipped  : " + (int)skippedTests + " (" + String.format("%05.2f", skipPercent) + "%)");
		System.out.println("==============================================================================================");
		System.out.println("==============================================================================================\r\n\r\n\r\n\r\n");
	}




	@Override
	public void onStart(ISuite suite)
	{
							suiteStartDate	= new Date();
		String 				suiteName		= suite.getXmlSuite().getName();
		SimpleDateFormat 	sdf 			= new SimpleDateFormat("EEE MM/dd/yyyy hh:mm:ss a zzz");
		String				formattedDate	= sdf.format(suiteStartDate);

		System.out.println("\r\n\r\n##############################################################################################");
		System.out.println("  TEST SUITE : " + suiteName);
		System.out.println("  STARTED    : " + formattedDate);
		System.out.println("##############################################################################################\r\n\r\n\r\n\r\n");
	}


	@Override
	public void onFinish(ISuite suite)
	{
		String				suiteName 		= suite.getXmlSuite().getName();
		double				passedTests		= 0;
		double				failedTests		= 0;
		double				skippedTests	= 0;
		SimpleDateFormat 	sdf 			= new SimpleDateFormat("EEE MM/dd/yyyy hh:mm:ss a zzz");
		Date 				endDate			= new Date();
		String				formattedDate	= sdf.format(endDate);
		String				suiteTimeTaken	= DurationFormatUtils.formatDuration(endDate.getTime() - suiteStartDate.getTime(), "HH 'hours' mm 'minutes' ss 'seconds'");

		for(Map.Entry<String, ISuiteResult> resultSet : suite.getResults().entrySet())
		{
			ITestContext 	testContext	  	= resultSet.getValue().getTestContext();

							passedTests    += testContext.getPassedTests().getAllResults().size();
							failedTests    += testContext.getFailedTests().getAllResults().size();
							skippedTests   += testContext.getSkippedTests().getAllResults().size();
		}

		double				totalTests		= passedTests + failedTests + skippedTests;
		double				passPercent		= (passedTests  * 100.00) / totalTests;
		double				failPercent		= (failedTests  * 100.00) / totalTests;
		double				skipPercent		= (skippedTests * 100.00) / totalTests;

		System.out.println("##############################################################################################");
		System.out.println("  TEST SUITE : " + suiteName + "\r\n");

		System.out.println("  Completed  : " + formattedDate);
		System.out.println("  Duration   : " + suiteTimeTaken + "\r\n");

		System.out.println("  XML   Tests   : " + suite.getResults().size() + "\r\n");
		System.out.println("  Total Tests   : " + (int)totalTests);
		System.out.println("  Total Passed  : " + (int)passedTests  + " (" + String.format("%05.2f", passPercent) + "%)");
		System.out.println("  Total Failed  : " + (int)failedTests  + " (" + String.format("%05.2f", failPercent) + "%)");
		System.out.println("  Total Skipped : " + (int)skippedTests + " (" + String.format("%05.2f", skipPercent) + "%)");
		System.out.println("##############################################################################################\r\n\r\n");
	}


	@Override
	public void alter(List<XmlSuite> suites)
	{
		//	more concise code, but if there is an error, the stacktrace would be much longer
//		suites.forEach(suite -> { suite.setVerbose(0); suite.getTests().forEach(test -> test.setVerbose(0)); });

		for(XmlSuite suite : suites)
		{
			suite.setVerbose(0);

			for(XmlTest test : suite.getTests())
			{
				//	If your tests are getting errors
				//	and you would like to see stacktraces
				//	set this value to 2
				test.setVerbose(2);
			}
		}
	}
}