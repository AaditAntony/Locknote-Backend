package com.locknote.locknote.repository;

import com.locknote.locknote.model.Note;
import com.locknote.locknote.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserAndDeletedFalse(User user);
}
