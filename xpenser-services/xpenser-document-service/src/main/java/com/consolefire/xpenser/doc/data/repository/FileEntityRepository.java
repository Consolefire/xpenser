package com.consolefire.xpenser.doc.data.repository;

import com.consolefire.xpenser.doc.data.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {


}
