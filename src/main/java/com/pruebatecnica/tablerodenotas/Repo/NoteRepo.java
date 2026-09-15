
package com.pruebatecnica.tablerodenotas.Repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.pruebatecnica.tablerodenotas.entidades.Note;

 

public interface NoteRepo extends JpaRepository<Note, Long> {
    

}