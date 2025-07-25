package com.shoestore.shoestoreWeb.repository;

import com.shoestore.shoestoreWeb.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface PermissionRepository extends JpaRepository<Permission, String> {

}
