package com.locknote.locknote.controller;

import com.locknote.locknote.dto.NoteRequest;
import com.locknote.locknote.model.Note;
import com.locknote.locknote.service.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public Note create(@RequestBody NoteRequest request,
                       Authentication authentication) {

        return noteService.createNote(
                request,
                authentication.getName()
        );
    }

    @GetMapping
    public List<Note> list(Authentication authentication) {
        return noteService.getNotes(authentication.getName());
    }
}
