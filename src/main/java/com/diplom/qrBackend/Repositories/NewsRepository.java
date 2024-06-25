package com.diplom.qrBackend.Repositories;


import com.diplom.qrBackend.Models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

}

