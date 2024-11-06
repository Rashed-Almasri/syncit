package com.syncit.service.commands;

import com.syncit.DTO.RunCodeDTO;
import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.service.CodeRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExecuteCommand implements Command{
    @Autowired
    CodeRunnerService codeRunnerService;
    @Autowired
    HelpCommand helpCommand;

    @Override
    public String execute(TerminalCommandDTO commandDTO) {
        try{
            return codeRunnerService.run(new RunCodeDTO(commandDTO.getFileId(), commandDTO.getExtension()));
        }
        catch(Exception e){
            return helpCommand.execute(commandDTO);
        }
    }
}
