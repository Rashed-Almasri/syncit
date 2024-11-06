package com.syncit.service.commands;

import com.syncit.DTO.TerminalCommandDTO;
import com.syncit.model.Commit;
import com.syncit.model.Project;
import com.syncit.repository.CommitRepository;
import com.syncit.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogCommand implements Command{
    @Autowired
    CommitRepository commitRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    HelpCommand helpCommand;
    @Override
    public String execute(TerminalCommandDTO commandDTO) {
        try{
            Project project = projectRepository.findById(commandDTO.getProjectId()).get();
            List<Commit> commits = commitRepository.findByProject(project);
            StringBuilder res = new StringBuilder();
            for (Commit commit : commits) {
                res.append("----------------------------\n");
                res.append("Commit id: " + commit.getId() + "\n");
                res.append("Commited By: " + commit.getUser().getUsername() + "\n");
                res.append("Commited in: " + commit.getCreatedAt() + "\n");
                res.append("Comment: " + commit.getMessage() + "\n");
            }

            if(res.length() > 0){
                res.append("----------------------------\n");
            }
            else{
                res.append("No commits found");
            }
            return res.toString();
        }
        catch(Exception e) {
            return helpCommand.execute(commandDTO);
        }
    }
}
