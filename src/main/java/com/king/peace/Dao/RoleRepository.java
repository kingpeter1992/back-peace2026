package com.king.peace.Dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.king.peace.Entitys.*;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	  Optional<Role> findByName(ERole name);

}
