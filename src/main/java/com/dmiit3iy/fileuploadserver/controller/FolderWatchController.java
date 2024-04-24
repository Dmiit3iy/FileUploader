package com.dmiit3iy.fileuploadserver.controller;

import com.dmiit3iy.fileuploadserver.events.FolderChangeEvent;
import com.dmiit3iy.fileuploadserver.service.FolderWatchService;
import com.dmiit3iy.fileuploadserver.service.SseEmitters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/")
public class FolderWatchController implements ApplicationListener<FolderChangeEvent> {

    @Value("${server.root}")
    private String path;
    final private FolderWatchService folderWatchService;

    private SseEmitters emitters;

    @Autowired
    public void setEmitters(SseEmitters emitters) {
        this.emitters = emitters;
    }

    public FolderWatchController(FolderWatchService folderWatchService) {
        this.folderWatchService = folderWatchService;
    }

    @PostConstruct
    void init() {
        folderWatchService.start(path);
    }

    @GetMapping(path = "/folder-watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getFolderWatch() {
        return emitters.add(new SseEmitter(60000L));
    }

    @Override
    public void onApplicationEvent(FolderChangeEvent event) {
        emitters.send(event.getEvent());
    }
}
