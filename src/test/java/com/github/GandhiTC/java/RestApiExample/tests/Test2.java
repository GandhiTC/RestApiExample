package com.github.GandhiTC.java.RestApiExample.tests;



import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



public class Test2 extends Base
{
	// BeforeSuite
	@Parameters({"maxListSize"})
	@BeforeSuite
	private void setMyMaxListSize(int outputWindowListMaxSize)
	{
		setMaxListSize(outputWindowListMaxSize);
	}


	//	Test 1
	@Test(priority = 1, enabled = true)
	public void testCreateEmployee()
	{
		createEmployee(false);
	}


	//	Test 2
	@Test(priority=2, enabled=true, dependsOnMethods= {"testCreateEmployee"})
	public void testGetSingleEmployee()
	{
		getSingleEmployee();
	}


	//	Test 3
	@Test(priority=3, enabled=true)
	public void testNegativeCreateEmployee()
	{
		createEmployee(true);
	}


	//	Test 4
	@Test(priority=4, enabled=true)
	public void testGetAllEmployees()
	{
		getAllEmployees();

		if(inEmployeeList)
		{
			System.out.println("\r\n\t" + empName + " successfully found in employee list!");
		}
		else
		{
			printFailure("\r\n\t" + empName + " is not in the employee list!", "ERROR:  EMPLOYEE WAS NOT FOUND IN THE EMPLOYEE LIST!");
		}
	}
}