package com.syncit.controller;

import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.service.commands.CommandInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/terminal")
@CrossOrigin("*")
public class TerminalController {
    @Autowired
    CommandInvoker commandInvoker;

    @PostMapping
    public ResponseEntity<?> executeCommand(@RequestBody TerminalCommandDTO command){
        return ResponseEntity.ok().body(commandInvoker.executeCommand(command));
    }
}
