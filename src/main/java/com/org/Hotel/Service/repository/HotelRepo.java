package com.org.Hotel.Service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.Hotel.Service.entities.Hotel;

@Repository
public interface HotelRepo extends JpaRepository<Hotel, String> {
}

