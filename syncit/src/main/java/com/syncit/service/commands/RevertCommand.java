package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.model.Project;
import com.syncit.repository.ProjectRepository;
import com.syncit.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevertCommand implements Command{
    @Autowired
    CommitService commitService;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public String execute(TerminalCommandDTO commandDTO) {
        try{
            String[] command = commandDTO.getCommand().split("\\s+");
            Project project = projectRepository.findById(commandDTO.getProjectId()).get();
            commitService.revertToCommit(project, Long.valueOf(command[2]));
            return "Reverted Successfully, Refresh the page to see the changes";
        }
        catch(Exception e){
            return "ERROR while reverting commit";
        }
    }
}
