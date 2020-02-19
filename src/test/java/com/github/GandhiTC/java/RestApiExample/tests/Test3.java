package com.github.GandhiTC.java.RestApiExample.tests;



import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;



public class Test3 extends Base
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
	public void testDeleteAllEmployees()
	{
		deleteAllEmployees();
	}


	//	Test 2
	@Test(priority=2, enabled=true)
	public void testGetAllEmployees()
	{
		getAllEmployees();

		if(inEmployeeList)
		{
			printFailure("\r\n\t" + empName + " is still in the employee list!", "ERROR:  EMPLOYEE WAS NOT REMOVED FROM EMPLOYEE LIST!");
		}
		else
		{
			System.out.println("\r\n\t" + empName + " successfully removed from employee list!");
		}
	}
}