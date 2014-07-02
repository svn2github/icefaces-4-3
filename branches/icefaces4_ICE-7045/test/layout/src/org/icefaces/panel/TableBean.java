package org.icefaces.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class TableBean implements Serializable
{
    private static final long serialVersionUID = 1L;

	private List<Employee> employees;
	
	public TableBean()
	{
		employees = new ArrayList<Employee>();
		
		Employee emp = new Employee("Armin");
		employees.add(emp);
		
		Employee emp1 = new Employee("Markus");
		employees.add(emp1);
		
		Employee emp2 = new Employee("Gareth");
		employees.add(emp2);
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
