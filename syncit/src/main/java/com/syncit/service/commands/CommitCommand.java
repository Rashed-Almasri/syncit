package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.model.Project;
import com.syncit.model.User;
import com.syncit.repository.ProjectRepository;
import com.syncit.repository.UserRepository;
import com.syncit.security.SecurityUtil;
import com.syncit.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommitCommand implements Command {
    @Autowired
    CommitService commitService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Override
    public String execute(TerminalCommandDTO command) {
        try{
            String[] commandList = command.getCommand().split("\\s+");
            User user = userRepository.findByUsername(SecurityUtil.getCurrentUsername()).get();
            Project project = projectRepository.findById(command.getProjectId()).get();
            String message;
            if(commandList.length > 2){
                StringBuilder messageBuilder = new StringBuilder();
                for(int i = 2; i < commandList.length; i++){
                    messageBuilder.append(" " + commandList[i]);
                }
                message = messageBuilder.toString();
            }
            else{
                message = "No Message";
            }
            commitService.createCommit(user, project, message);

            return "Commited Successfully";
        }
        catch(Exception e){
            return "ERROR: " + e.getMessage();
        }
    }

}
