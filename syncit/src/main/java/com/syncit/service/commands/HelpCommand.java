package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {
    @Override
    public String execute(TerminalCommandDTO commandDTO) {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:\n");
        sb.append("log | shows commits log\n");
        sb.append("commit | creates commit\n");
        sb.append("add [file path or . to add all changes | adds changes to commit\n");
        sb.append("revert [commit id] | reverts to a specific commit\n");

        return sb.toString();
    }
}
