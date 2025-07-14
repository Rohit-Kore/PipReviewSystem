package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.entity.Employee;
import com.example.PipReviewSystem.entity.PIP;
import com.example.PipReviewSystem.repository.EmployeeRepository;
import com.example.PipReviewSystem.repository.PipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PIPServiceImpl implements PIPService {

    @Autowired
    private PipRepository pipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ResponseEntity<?> startPip(PIP pip) {
        Optional<Employee> employee = employeeRepository.findById(pip.getEmployee().getEmployeeId());
        Optional<Employee> reviewer = employeeRepository.findById(pip.getReviewer().getEmployeeId());

        if (employee.isEmpty() || reviewer.isEmpty()) {
            return new ResponseEntity<>("Employee or Reviewer not found", HttpStatus.NOT_FOUND);
        }

        pip.setEmployee(employee.get());
        pip.setReviewer(reviewer.get());
        pip.setStatus("ACTIVE");

        PIP savedPip = pipRepository.save(pip);
        return new ResponseEntity<>(savedPip, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> updatePip(Long pipId, PIP pip) {
        Optional<PIP> existing = pipRepository.findById(pipId);
        if (existing.isEmpty()) {
            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);
        }

        PIP updated = existing.get();
        updated.setProgress(pip.getProgress());
        updated.setGoals(pip.getGoals());
        updated.setEndDate(pip.getEndDate());
        updated.setStatus(pip.getStatus());
        updated.setComments(pip.getComments());

        return new ResponseEntity<>(pipRepository.save(updated), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> completePip(Long pipId, String outcome) {
        Optional<PIP> pip = pipRepository.findById(pipId);
        if (pip.isEmpty()) {
            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);
        }

        PIP p = pip.get();
        p.setStatus("COMPLETED");
        p.setOutcome(outcome);
        return new ResponseEntity<>(pipRepository.save(p), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getPipsByEmployee(UUID employeeId) {
        List<PIP> pips = pipRepository.findByEmployee_EmployeeId(employeeId);
        if (pips.isEmpty()) {
            return new ResponseEntity<>("No PIPs found for this employee", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pips, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getAllPips() {
        List<PIP> all = pipRepository.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }
}
