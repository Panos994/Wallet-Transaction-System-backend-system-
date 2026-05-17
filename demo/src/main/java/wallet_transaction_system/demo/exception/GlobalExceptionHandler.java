package wallet_transaction_system.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wallet_transaction_system.demo.dto.ApiError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler  {

    @ExceptionHandler(ConcurrencyException.class)
    public ResponseEntity<ApiError> handleConcurrencyException(ConcurrencyException ex, HttpServletRequest status){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("CONCURRENCY_CONFLICT")
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(status.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex, HttpServletRequest status){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(status.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);

    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex, HttpServletRequest status){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("FORBIDDEN")
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(status.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest status){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("UNAUTHORIZED")
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(status.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest status){
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("BUSINESS_ERROR")
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .path(status.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    /* Συντομη περιγραφή για τα exceptions μου:
    BadRequestException (400)
Όταν το αίτημα είναι άκυρο ή δεν περνάει validation — π.χ. απαραίτητο πεδίο λείπει, λάθος μορφή JSON ή μη έγκυρη παράμετρος.

NotFoundException (404)
Όταν το ζητούμενο resource δεν υπάρχει — π.χ. χρήστης/πορτοφόλι/συναλλαγή με το δοθέν id δεν βρέθηκε.

UnauthorizedException (401)
Όταν ο χρήστης δεν είναι authenticated ή το token/credentials είναι άκυρα/λήξαντα — π.χ. απουσία ή κακό JWT.

ForbiddenException (403)
Όταν ο χρήστης είναι authenticated αλλά δεν έχει άδεια για την ενέργεια — π.χ. προσπαθεί να δει/τροποποιήσει πόρους άλλου χρήστη χωρίς δικαιώματα.

ConcurrencyException (409)
Όταν υπάρχει σύγκρουση ταυτόχρονων ενημερώσεων — π.χ. optimistic locking failure, version mismatch.

BusinessException (422 ή 400/409)
Όταν παραβιάζεται κανόνας της επιχειρησιακής λογικής — π.χ. απόπειρα ανάληψης που υπερβαίνει το υπόλοιπο, όριο μεταφοράς, ή μη έγκυρη κατάσταση συναλλαγής. (Κωδικός μπορεί να είναι 422 Unprocessable Entity, ή 400/409 ανά περίπτωση.)
    */


}
