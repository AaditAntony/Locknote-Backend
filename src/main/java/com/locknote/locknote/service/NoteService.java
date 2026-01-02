package com.locknote.locknote.service;

import com.locknote.locknote.dto.NoteRequest;
import com.locknote.locknote.model.Note;
import com.locknote.locknote.model.User;
import com.locknote.locknote.repository.NoteRepository;
import com.locknote.locknote.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.locknote.locknote.config.AesEncryptionUtil;
import org.springframework.beans.factory.annotation.Value;


import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    @Value("${app.encryption.secret}")
    private String encryptionSecret;


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
        String encryptedContent =
                AesEncryptionUtil.encrypt(request.getContent(), encryptionSecret);

        note.setContent(encryptedContent);

        note.setUser(user);

        return noteRepository.save(note);
    }

    public List<Note> getNotes(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Note> notes = noteRepository.findByUserAndDeletedFalse(user);

        notes.forEach(note -> {
            String decrypted =
                    AesEncryptionUtil.decrypt(note.getContent(), encryptionSecret);
            note.setContent(decrypted);
        });

        return notes;

    }
}
