package com.example.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository <Image, Long>{
	
	@Query("from Image where user_id = :userId")
	Image getByUserId (Long userId);
	
	@Modifying
	@Transactional
	@Query("Delete from Image where user_id = :userId")
	Integer deleteByUserId (Long userId);
}
		
