package com.example.PipReviewSystem.controller;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Employee employee) {
        return employeeService.registerEmployee(employee);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        return employeeService.login(email, password);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return employeeService.getAllEmployees();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'HR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return employeeService.deleteEmployee(id);
    }
}























//@RestController
//@RequestMapping("/api/employees")
//public class EmployeeController {
//
//    @Autowired
//    private EmployeeService employeeService;
//
//    public EmployeeController(EmployeeService employeeService) {
//        this.employeeService = employeeService;
//    }
//
//    @PostMapping
//    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
//        return ResponseEntity.ok(employeeService.createEmployee(employee));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
//        return ResponseEntity.ok(employeeService.getEmployeeById(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Employee>> getAllEmployees() {
//        return ResponseEntity.ok(employeeService.getAllEmployees());
//    }
//
//    @GetMapping("/role/{role}")
//    public ResponseEntity<List<Employee>> getEmployeesByRole(@PathVariable Role role) {
//        return ResponseEntity.ok(employeeService.getEmployeesByRole(role));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Employee> updateEmployee(@PathVariable UUID id, @RequestBody Employee employee) {
//        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
//        employeeService.deleteEmployee(id);
//        return ResponseEntity.noContent().build();
//    }
//}
