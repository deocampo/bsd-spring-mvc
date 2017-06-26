package com.deocampo.barebones.springmvc.service;

import java.util.Collection;

import com.deocampo.barebones.springmvc.Employee;

public interface EmployeeService {

	public Collection<Employee> get();

	public Employee get(Long id);

	public Employee save(Employee employee);

	public void delete(Long id);
}
