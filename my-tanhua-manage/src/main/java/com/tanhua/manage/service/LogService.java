package com.tanhua.manage.service;

import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.pojo.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private LogMapper logMapper;
    public void save(Log log) {
        logMapper.insert(log);
    }
}
