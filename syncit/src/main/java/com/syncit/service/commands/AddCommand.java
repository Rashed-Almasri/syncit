package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.model.Project;
import com.syncit.repository.ProjectRepository;
import com.syncit.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddCommand implements Command {
    @Autowired
    HelpCommand helpCommand;
    @Autowired
    CommitService commitService;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public String execute(TerminalCommandDTO commandDTO) {
        String[] command = commandDTO.getCommand().split("\\s+");
        try{
            Project project = projectRepository.findById(commandDTO.getProjectId()).get();
            commitService.addToCommit(project, command[2]);
            return "Added Successfully";
        }
        catch (Exception e){
            return helpCommand.execute(commandDTO);
        }
    }
}
