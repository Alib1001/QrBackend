package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.Room;
import com.diplom.qrBackend.Repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomRepository.save(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomRepository.findAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer id) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        return roomOptional.map(room -> new ResponseEntity<>(room, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Integer id, @RequestBody Room roomDetails) {
        Optional<Room> roomOptional = roomRepository.findById(id);
        return roomOptional.map(room -> {
            room.setRoomName(roomDetails.getRoomName());
            room.setBuilding(roomDetails.getBuilding());
            room.setBusy(roomDetails.getBusy());
            Room updatedRoom = roomRepository.save(room);
            return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRoom(@PathVariable Integer id) {
        try {
            roomRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/free")
    public ResponseEntity<List<Room>> getAllFreeRooms() {
        List<Room> freeRooms = roomRepository.findByBusyFalse();
        return new ResponseEntity<>(freeRooms, HttpStatus.OK);
    }

    @PostMapping("/addRooms")
    public ResponseEntity<String> addRooms() {
        String[] roomNumbers = {"01", "02", "03", "04", "05", "06", "07", "08", "09"};
        String building = "Baizak";
        String roomStr = "Room";
        try {
            for (int floor = 2; floor <= 4; floor++) {
                for (String roomNumber : roomNumbers) {
                    Room room = new Room();
                    room.setRoomName(roomStr + " " + floor + roomNumber);
                    room.setBuilding(building);
                    room.setBusy(false);
                    roomRepository.save(room);
                }
            }
            return new ResponseEntity<>("Rooms added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add rooms", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
