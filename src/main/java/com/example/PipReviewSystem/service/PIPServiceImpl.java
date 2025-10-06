package com.example.PipReviewSystem.service;

import com.example.PipReviewSystem.dto.PipDTO;

import com.example.PipReviewSystem.dto.ProgressUpdateDTO;

import com.example.PipReviewSystem.entity.Employee;

import com.example.PipReviewSystem.entity.PIP;

import com.example.PipReviewSystem.repository.EmployeeRepository;

import com.example.PipReviewSystem.repository.PipRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.Arrays;

import java.util.List;

import java.util.Objects;

import java.util.Optional;

import java.util.UUID;

import java.util.stream.Collectors;

@Service

public class PIPServiceImpl implements PIPService {

    @Autowired

    private PipRepository pipRepository;

    @Autowired

    private EmployeeRepository employeeRepository;

    @Autowired

    private NotificationService notificationService;

    // ✅ Mapper: Convert PIP Entity → PipDTO

    private PipDTO mapToDTO(PIP pip) {

        PipDTO dto = new PipDTO();

        dto.setPipId(pip.getPipId());

        if (pip.getEmployee() != null) {

            dto.setEmployeeId(pip.getEmployee().getEmployeeId());

            dto.setEmployeeName(pip.getEmployee().getName());

        }

        if (pip.getReviewer() != null) {

            dto.setReviewerId(pip.getReviewer().getEmployeeId());

            dto.setReviewerName(pip.getReviewer().getName());

        }

        dto.setStartDate(pip.getStartDate());

        dto.setEndDate(pip.getEndDate());

        dto.setGoals(pip.getGoals());

        dto.setProgress(pip.getProgress());

        dto.setStatus(pip.getStatus());

        dto.setOutcome(pip.getOutcome());

        dto.setComments(pip.getComments());

        return dto;

    }

    /**

     * Starts a new Performance Improvement Plan (PIP).

     */

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

        // Notifications

        String employeeNotificationMessage = "You have been placed on a Performance Improvement Plan. Please check your goals.";

        String reviewerNotificationMessage = "A new PIP has been started for " + savedPip.getEmployee().getName() + ".";

        notificationService.createNotification(savedPip.getEmployee(), "PIP Started", employeeNotificationMessage, "ALERT");

        notificationService.createNotification(savedPip.getReviewer(), "New PIP", reviewerNotificationMessage, "ALERT");

        return new ResponseEntity<>(mapToDTO(savedPip), HttpStatus.CREATED);

    }

    /**

     * Updates an existing Performance Improvement Plan (PIP).

     */

    @Override

    public ResponseEntity<?> updatePip(Long pipId, PIP pip) {

        Optional<PIP> existing = pipRepository.findById(pipId);

        if (existing.isEmpty()) {

            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);

        }

        PIP updated = existing.get();

        boolean changesMade = false;

        if (!Objects.equals(updated.getProgress(), pip.getProgress())) {

            updated.setProgress(pip.getProgress());

            changesMade = true;

        }

        if (!Objects.equals(updated.getGoals(), pip.getGoals())) {

            updated.setGoals(pip.getGoals());

            changesMade = true;

        }

        if (!Objects.equals(updated.getEndDate(), pip.getEndDate())) {

            updated.setEndDate(pip.getEndDate());

            changesMade = true;

        }

        if (!Objects.equals(updated.getStatus(), pip.getStatus())) {

            updated.setStatus(pip.getStatus());

            changesMade = true;

        }

        if (!Objects.equals(updated.getComments(), pip.getComments())) {

            updated.setComments(pip.getComments());

            changesMade = true;

        }

        PIP savedPip = pipRepository.save(updated);

        // Notifications only if changes made

        if (changesMade) {

            String employeeNotificationTitle = "PIP Update";

            String employeeNotificationMessage =

                    "Your Performance Improvement Plan has been updated. Status: " + savedPip.getStatus();

            notificationService.createNotification(savedPip.getEmployee(), employeeNotificationTitle,

                    employeeNotificationMessage, "INFO");

            String reviewerNotificationTitle = "PIP Update for " + savedPip.getEmployee().getName();

            String reviewerNotificationMessage =

                    savedPip.getEmployee().getName() + "'s PIP has been updated. Status: " + savedPip.getStatus();

            notificationService.createNotification(savedPip.getReviewer(), reviewerNotificationTitle,

                    reviewerNotificationMessage, "INFO");

        }

        return new ResponseEntity<>(mapToDTO(savedPip), HttpStatus.OK);

    }

    /**

     * Completes an existing Performance Improvement Plan (PIP).

     */

    @Override

    public ResponseEntity<?> completePip(Long pipId, String outcome) {

        Optional<PIP> pip = pipRepository.findById(pipId);

        if (pip.isEmpty()) {

            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);

        }

        PIP p = pip.get();

        p.setStatus("COMPLETED");

        p.setOutcome(outcome);

        PIP savedPip = pipRepository.save(p);

        // Notifications

        String notificationTitle = "PIP Completed";

        String notificationMessage =

                "Your Performance Improvement Plan has been completed with the outcome: " + outcome;

        notificationService.createNotification(savedPip.getEmployee(), notificationTitle, notificationMessage, "ALERT");

        String reviewerNotificationTitle = "PIP Completed for " + savedPip.getEmployee().getName();

        String reviewerNotificationMessage =

                savedPip.getEmployee().getName() + "'s PIP has been completed with outcome: " + outcome;

        notificationService.createNotification(savedPip.getReviewer(), reviewerNotificationTitle,

                reviewerNotificationMessage, "INFO");

        return new ResponseEntity<>(mapToDTO(savedPip), HttpStatus.OK);

    }

    /**

     * Retrieves all PIPs by employee.

     */

    @Override

    public ResponseEntity<?> getPipsByEmployee(UUID employeeId) {

        List<PIP> pips = pipRepository.findByEmployee_EmployeeId(employeeId);

        if (pips.isEmpty()) {

            return new ResponseEntity<>("No PIPs found for this employee", HttpStatus.NOT_FOUND);

        }

        List<PipDTO> dtos = pips.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);

    }

    /**

     * Retrieves all PIPs in the system.

     */

    @Override

    public ResponseEntity<?> getAllPips() {

        List<PIP> all = pipRepository.findAll();

        List<PipDTO> dtos = all.stream().map(this::mapToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);

    }



    @Override

    public ResponseEntity<?> trackProgress(Long pipId, ProgressUpdateDTO dto) {

        Optional<PIP> existing = pipRepository.findById(pipId);

        if (existing.isEmpty()) {

            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);

        }

        List<String> validStatuses = Arrays.asList("ASSIGNED", "STARTED", "WORKING", "ABOUT TO COMPLETE", "COMPLETED");

        if (!validStatuses.contains(dto.getStatus().toUpperCase())) {

            return new ResponseEntity<>("Invalid status provided. Valid statuses are: " + validStatuses,

                    HttpStatus.BAD_REQUEST);

        }

        PIP pip = existing.get();

        String currentProgress = pip.getProgress() != null ? pip.getProgress() : "";

        String newProgressEntry = "[" + LocalDateTime.now() + "] Status changed to " + dto.getStatus().toUpperCase()

                + ". " + dto.getProgressNote();

        pip.setProgress((currentProgress.isEmpty() ? "" : currentProgress + "\n") + newProgressEntry);

        pip.setStatus(dto.getStatus().toUpperCase());

        pip.setLastProgressReviewDate(LocalDateTime.now());

        PIP savedPip = pipRepository.save(pip);

        // Notification

        String notificationTitle = "PIP Progress Update";

        String notificationMessage = "Your PIP status has been updated to: " + dto.getStatus().toUpperCase();

        notificationService.createNotification(savedPip.getEmployee(), notificationTitle, notificationMessage, "INFO");

        return new ResponseEntity<>(mapToDTO(savedPip), HttpStatus.OK);

    }

}










//package com.example.PipReviewSystem.service;
//
//import com.example.PipReviewSystem.dto.ProgressUpdateDTO;
//import com.example.PipReviewSystem.entity.Employee;
//import com.example.PipReviewSystem.entity.PIP;
//import com.example.PipReviewSystem.repository.EmployeeRepository;
//import com.example.PipReviewSystem.repository.PipRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects; // Added for Objects.equals comparison
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class PIPServiceImpl implements PIPService {
//
//    @Autowired
//    private PipRepository pipRepository;
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Autowired
//    private NotificationService notificationService; // NotificationService injected
//
//    /**
//     * Starts a new Performance Improvement Plan (PIP) for an employee.
//     * Sends notifications to both the employee and the reviewer.
//     *
//     * @param pip The PIP entity containing details for the new PIP.
//     * @return ResponseEntity with the created PIP or an error message.
//     */
//    @Override
//    public ResponseEntity<?> startPip(PIP pip) {
//        Optional<Employee> employee = employeeRepository.findById(pip.getEmployee().getEmployeeId());
//        Optional<Employee> reviewer = employeeRepository.findById(pip.getReviewer().getEmployeeId());
//
//        if (employee.isEmpty() || reviewer.isEmpty()) {
//            return new ResponseEntity<>("Employee or Reviewer not found", HttpStatus.NOT_FOUND);
//        }
//
//        pip.setEmployee(employee.get());
//        pip.setReviewer(reviewer.get());
//        pip.setStatus("ACTIVE"); // Set initial status
//
//        PIP savedPip = pipRepository.save(pip);
//
//        // Notification call: PIP started for employee and reviewer
//        String employeeNotificationMessage = "You have been placed on a Performance Improvement Plan. Please check your goals.";
//        String reviewerNotificationMessage = "A new PIP has been started for " + savedPip.getEmployee().getName() + ".";
//        notificationService.createNotification(savedPip.getEmployee(), "PIP Started", employeeNotificationMessage, "ALERT");
//        notificationService.createNotification(savedPip.getReviewer(), "New PIP", reviewerNotificationMessage, "ALERT");
//
//        return new ResponseEntity<>(savedPip, HttpStatus.CREATED);
//    }
//
//    /**
//     * Updates an existing Performance Improvement Plan (PIP).
//     * Sends notifications to both the employee and the reviewer if changes are made.
//     *
//     * @param pipId The ID of the PIP to update.
//     * @param pip The PIP entity containing updated details.
//     * @return ResponseEntity with the updated PIP or an error message.
//     */
//    @Override
//    public ResponseEntity<?> updatePip(Long pipId, PIP pip) {
//        Optional<PIP> existing = pipRepository.findById(pipId);
//        if (existing.isEmpty()) {
//            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);
//        }
//
//        PIP updated = existing.get();
//        boolean changesMade = false; // Flag to track if any changes occurred
//
//        // Check and update fields, setting changesMade flag if a change happens
//        if (!Objects.equals(updated.getProgress(), pip.getProgress())) {
//            updated.setProgress(pip.getProgress());
//            changesMade = true;
//        }
//        if (!Objects.equals(updated.getGoals(), pip.getGoals())) {
//            updated.setGoals(pip.getGoals());
//            changesMade = true;
//        }
//        if (!Objects.equals(updated.getEndDate(), pip.getEndDate())) {
//            updated.setEndDate(pip.getEndDate());
//            changesMade = true;
//        }
//        if (!Objects.equals(updated.getStatus(), pip.getStatus())) {
//            updated.setStatus(pip.getStatus());
//            changesMade = true;
//        }
//        if (!Objects.equals(updated.getComments(), pip.getComments())) {
//            updated.setComments(pip.getComments());
//            changesMade = true;
//        }
//
//        PIP savedPip = pipRepository.save(updated);
//
//        // Notification call: PIP updated (ADDED)
//        if (changesMade) { // Only send notification if actual changes were made
//            String employeeNotificationTitle = "PIP Update";
//            String employeeNotificationMessage = "Your Performance Improvement Plan has been updated. Status: " + savedPip.getStatus();
//            notificationService.createNotification(savedPip.getEmployee(), employeeNotificationTitle, employeeNotificationMessage, "INFO");
//
//            String reviewerNotificationTitle = "PIP Update for " + savedPip.getEmployee().getName();
//            String reviewerNotificationMessage = savedPip.getEmployee().getName() + "'s PIP has been updated. Status: " + savedPip.getStatus();
//            notificationService.createNotification(savedPip.getReviewer(), reviewerNotificationTitle, reviewerNotificationMessage, "INFO");
//        }
//
//        return new ResponseEntity<>(savedPip, HttpStatus.OK);
//    }
//
//    /**
//     * Completes an existing Performance Improvement Plan (PIP).
//     * Sends a notification to the employee upon completion.
//     *
//     * @param pipId The ID of the PIP to complete.
//     * @param outcome The outcome of the PIP (e.g., "SUCCESS", "FAILED").
//     * @return ResponseEntity with the completed PIP or an error message.
//     */
//    @Override
//    public ResponseEntity<?> completePip(Long pipId, String outcome) {
//        Optional<PIP> pip = pipRepository.findById(pipId);
//        if (pip.isEmpty()) {
//            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);
//        }
//
//        PIP p = pip.get();
//        p.setStatus("COMPLETED");
//        p.setOutcome(outcome);
//
//        PIP savedPip = pipRepository.save(p);
//        // Notification call: PIP completed
//        String notificationTitle = "PIP Completed";
//        String notificationMessage = "Your Performance Improvement Plan has been completed with the outcome: " + outcome;
//        notificationService.createNotification(savedPip.getEmployee(), notificationTitle, notificationMessage, "ALERT");
//
//        // Notification to reviewer about PIP completion
//        String reviewerNotificationTitle = "PIP Completed for " + savedPip.getEmployee().getName();
//        String reviewerNotificationMessage = savedPip.getEmployee().getName() + "'s PIP has been completed with outcome: " + outcome;
//        notificationService.createNotification(savedPip.getReviewer(), reviewerNotificationTitle, reviewerNotificationMessage, "INFO");
//
//
//        return new ResponseEntity<>(savedPip, HttpStatus.OK); // Return savedPip not just p
//    }
//
//    /**
//     * Retrieves all PIPs associated with a specific employee.
//     *
//     * @param employeeId The UUID of the employee.
//     * @return ResponseEntity with a list of PIPs or a NOT_FOUND message.
//     */
//    @Override
//    public ResponseEntity<?> getPipsByEmployee(UUID employeeId) {
//        List<PIP> pips = pipRepository.findByEmployee_EmployeeId(employeeId);
//        if (pips.isEmpty()) {
//            return new ResponseEntity<>("No PIPs found for this employee", HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(pips, HttpStatus.OK);
//    }
//
//    /**
//     * Retrieves all PIPs in the system.
//     *
//     * @return ResponseEntity with a list of all PIPs.
//     */
//    @Override
//    public ResponseEntity<?> getAllPips() {
//        List<PIP> all = pipRepository.findAll();
//        return new ResponseEntity<>(all, HttpStatus.OK);
//    }
//
//    /**
//     * Tracks the progress of an ongoing PIP.
//     * Adds a new progress entry and updates the PIP status.
//     * Sends a notification to the employee when progress is updated.
//     *
//     * @param pipId The ID of the PIP to track.
//     * @param dto The ProgressUpdateDTO containing progress details.
//     * @return ResponseEntity with the updated PIP or an error message.
//     * @throws RuntimeException if PIP is not found or status is invalid.
//     */
//    @Override
//    public ResponseEntity<?> trackProgress(Long pipId, ProgressUpdateDTO dto) {
//        Optional<PIP> existing = pipRepository.findById(pipId);
//        if (existing.isEmpty()) {
//            return new ResponseEntity<>("PIP not found", HttpStatus.NOT_FOUND);
//        }
//
//        List<String> validStatuses = Arrays.asList("ASSIGNED", "STARTED", "WORKING", "ABOUT TO COMPLETE", "COMPLETED");
//        if (!validStatuses.contains(dto.getStatus().toUpperCase())) {
//            return new ResponseEntity<>("Invalid status provided. Valid statuses are: " + validStatuses, HttpStatus.BAD_REQUEST);
//        }
//
//        PIP pip = existing.get();
//
//        String currentProgress = pip.getProgress() != null ? pip.getProgress() : "";
//        String newProgressEntry = "[" + LocalDateTime.now() + "] Status changed to " + dto.getStatus().toUpperCase() + ". " + dto.getProgressNote();
//        pip.setProgress((currentProgress.isEmpty() ? "" : currentProgress + "\n") + newProgressEntry);
//
//        pip.setStatus(dto.getStatus().toUpperCase());
//
//        pip.setLastProgressReviewDate(LocalDateTime.now());
//
//        PIP savedPip = pipRepository.save(pip);
//
//        // Notification call: PIP progress updated
//        String notificationTitle = "PIP Progress Update";
//        String notificationMessage = "Your PIP status has been updated to: " + dto.getStatus().toUpperCase();
//        notificationService.createNotification(savedPip.getEmployee(), notificationTitle, notificationMessage, "INFO");
//
//        return new ResponseEntity<>(savedPip, HttpStatus.OK);
//    }
//
//}
//
//
