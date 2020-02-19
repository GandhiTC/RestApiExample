package com.github.GandhiTC.java.RestApiExample.tests;



import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



public class Test1 extends Base
{
	// BeforeSuite
	@Parameters({"maxListSize"})
	@BeforeSuite
	private void setMyMaxListSize(int outputWindowListMaxSize)
	{
		setMaxListSize(outputWindowListMaxSize);
	}


	// Test 1
	@Test(priority = 1, enabled = true)
	public void testCreateEmployee()
	{
		createEmployee(false);
	}


	// Test 2
	@Test(priority = 2, enabled = true, dependsOnMethods= {"testCreateEmployee"})
	public void testGetSingleEmployee()
	{
		getSingleEmployee();
	}


	// Test 3
	@Test(priority = 3, enabled = true, dependsOnMethods= {"testCreateEmployee"})
	public void testUpdateEmployee()
	{
		updateEmployee();
	}


	// Test 4
	@Test(priority = 4, enabled = true, dependsOnMethods= {"testUpdateEmployee"})
	public void testVerifyUpdates()
	{
		getSingleEmployee();
	}


	// Test 5
	@Test(priority = 5, enabled = true, dependsOnMethods= {"testCreateEmployee"})
	public void testDeleteEmployee()
	{
		deleteEmployee();
	}


	// Test 6
	@Test(priority = 6, enabled = true)
	public void testGetAllEmployees()
	{
		getAllEmployees();

		if(inEmployeeList)
		{
			printFailure("\r\n\t" + empName + " is still in the employee list!",
							"ERROR:  EMPLOYEE WAS NOT REMOVED FROM EMPLOYEE LIST!");
		}
		else
		{
			System.out.println("\r\n\t" + empName + " successfully removed from employee list!");
		}
	}
}