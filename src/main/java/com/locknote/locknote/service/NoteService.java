package com.locknote.locknote.service;

import com.locknote.locknote.config.AesEncryptionUtil;
import com.locknote.locknote.dto.NoteRequest;
import com.locknote.locknote.model.Note;
import com.locknote.locknote.model.User;
import com.locknote.locknote.repository.NoteRepository;
import com.locknote.locknote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    // ================= CREATE NOTE =================
    public Note createNote(NoteRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setTitle(request.getTitle());

        // 1️⃣ Encrypt BEFORE saving to DB
        String encryptedContent =
                AesEncryptionUtil.encrypt(
                        request.getContent(),
                        encryptionSecret
                );

        note.setContent(encryptedContent);
        note.setUser(user);

        // 2️⃣ Save encrypted content
        Note savedNote = noteRepository.save(note);

        // 3️⃣ Decrypt BEFORE returning response
        String decryptedContent =
                AesEncryptionUtil.decrypt(
                        savedNote.getContent(),
                        encryptionSecret
                );

        savedNote.setContent(decryptedContent);

        return savedNote;
    }

    // ================= GET NOTES =================
    public List<Note> getNotes(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Note> notes = noteRepository.findByUserAndDeletedFalse(user);

        // Decrypt each note BEFORE returning
        notes.forEach(note -> {
            String decrypted =
                    AesEncryptionUtil.decrypt(
                            note.getContent(),
                            encryptionSecret
                    );
            note.setContent(decrypted);
        });

        return notes;
    }

    public Note updateNote(Long noteId, NoteRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // 🔒 Ownership check
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        note.setTitle(request.getTitle());

        String encryptedContent =
                AesEncryptionUtil.encrypt(
                        request.getContent(),
                        encryptionSecret
                );

        note.setContent(encryptedContent);

        Note updated = noteRepository.save(note);

        // Decrypt before return
        updated.setContent(
                AesEncryptionUtil.decrypt(
                        updated.getContent(),
                        encryptionSecret
                )
        );

        return updated;
    }

}
