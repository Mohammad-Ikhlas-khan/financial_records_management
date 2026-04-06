package com.ikhlas.finance.controller;

import com.ikhlas.finance.model.Record;
import com.ikhlas.finance.model.RecordType;
import com.ikhlas.finance.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/records")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRecord(@Valid @RequestBody Record record){
            recordService.addRecord(record);
            return ResponseEntity.ok("Record created successfully.");
    }

    @GetMapping("/view")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public List<Record> viewRecords(@RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate start,
                                    @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate end,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) RecordType type){
        return recordService.getRecords(start,end,category,type);
    }

    @PutMapping("/update/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRecord(@PathVariable long recordId,@Valid @RequestBody  Record record){
            recordService.updateRecord(recordId,record);
            return ResponseEntity.ok("Record updated Successfully.");
    }

    @DeleteMapping("delete/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRecord(@PathVariable long recordId){
        recordService.deleteRecord(recordId);
        return ResponseEntity.ok("Deleted the record with " + recordId + " successfully.");
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDashboard(){
        return ResponseEntity.ok(recordService.getSummary());
    }
}
