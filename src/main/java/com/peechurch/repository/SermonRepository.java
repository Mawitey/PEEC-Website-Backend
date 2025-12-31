package com.peechurch.repository;

import com.peechurch.model.Sermon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SermonRepository extends JpaRepository<Sermon, Long> {
    boolean existsByYoutubeId(String youtubeId);
}

