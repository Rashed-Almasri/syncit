package com.coderunner.service;

import org.springframework.stereotype.Service;

@Service
public class OsService {
    String getLowerCaseOsName(){
        return System.getProperty("os.name").toLowerCase();
    }
}
