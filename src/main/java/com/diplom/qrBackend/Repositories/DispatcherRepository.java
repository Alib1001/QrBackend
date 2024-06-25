package com.diplom.qrBackend.Repositories;



import com.diplom.qrBackend.Models.Dispatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatcherRepository extends JpaRepository<Dispatcher, Long> {
}
