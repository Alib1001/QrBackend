package com.diplom.qrBackend.Repositories;

import com.diplom.qrBackend.Models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByBusyFalse();
}
