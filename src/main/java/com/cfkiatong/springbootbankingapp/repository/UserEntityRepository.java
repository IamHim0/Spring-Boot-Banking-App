package com.cfkiatong.springbootbankingapp.repository;

import com.cfkiatong.springbootbankingapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
}
