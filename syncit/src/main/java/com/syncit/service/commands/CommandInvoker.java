package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandInvoker {
    private Map<String, Command> commandMap;
    @Autowired
    CommitCommand commitCommand;
    @Autowired
    HelpCommand helpCommand;
    @Autowired
    ExecuteCommand executeCommand;
    @Autowired
    LogCommand logCommand;
    @Autowired
    RevertCommand revertCommand;
    @Autowired
    AddCommand addCommand;
    public CommandInvoker() {
        this.commandMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        this.commandMap.put("commit", commitCommand);
        this.commandMap.put("help", helpCommand);
        this.commandMap.put("execute", executeCommand);
        this.commandMap.put("log", logCommand);
        this.commandMap.put("revert", revertCommand);
        this.commandMap.put("add", addCommand);
    }

    public String executeCommand(TerminalCommandDTO commandDTO) {
        try{
            String[] parts = commandDTO.getCommand().split("\\s+");
            Command command = commandMap.get(parts[1]);

            if (command != null) {
                return command.execute(commandDTO);
            } else {
                return commandMap.get("help").execute(commandDTO);
            }
        }
        catch (Exception e) {
            return commandMap.get("help").execute(commandDTO);
        }
    }
}