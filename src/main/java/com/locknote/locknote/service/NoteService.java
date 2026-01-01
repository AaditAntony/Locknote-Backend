package com.locknote.locknote.service;

import com.locknote.locknote.dto.NoteRequest;
import com.locknote.locknote.model.Note;
import com.locknote.locknote.model.User;
import com.locknote.locknote.repository.NoteRepository;
import com.locknote.locknote.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository,
                       UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public Note createNote(NoteRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);

        return noteRepository.save(note);
    }

    public List<Note> getNotes(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return noteRepository.findByUserAndDeletedFalse(user);
    }
}
