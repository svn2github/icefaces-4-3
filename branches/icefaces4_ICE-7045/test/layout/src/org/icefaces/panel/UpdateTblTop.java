package org.icefaces.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

@ManagedBean
@ViewScoped
public class UpdateTblTop implements Serializable
{
    private static final long serialVersionUID = 1L;

	private List<Employee> employees;
	private int count=0;
	private String empName="Armin";
	
	public void addToTop(ActionEvent event)
	{
		Employee emp = new Employee("Added:"+empName+count);
		employees.add(0,emp);
		count++;
	}
	
	public UpdateTblTop()
	{
		employees = new ArrayList<Employee>();
		
		//Add some employees to populate list
		Employee emp = new Employee(empName+count);
		employees.add(0,emp);
		count++;
		
		Employee emp1 = new Employee(empName+count);
		employees.add(0,emp1);
		count++;
		
		Employee emp2 = new Employee(empName+count);
		employees.add(0,emp2);
		count++;
	}

	public List<Employee> getEmployees()
	{
		return employees;
	}

	public void setEmployees(List<Employee> employees)
	{
		this.employees = employees;
	}
}
