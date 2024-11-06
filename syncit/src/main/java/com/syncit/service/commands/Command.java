package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;

public interface Command {
    public String execute(TerminalCommandDTO commandDTO);
}
