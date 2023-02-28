package com.salesianostriana.gamesforall.user.repo;

import com.salesianostriana.gamesforall.product.model.Product;
import com.salesianostriana.gamesforall.user.model.User;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findFirstByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u.favorites FROM User u WHERE u.id = :userId")
    Page<Product> findFavoriteProductsByUserId(UUID userId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    Page<Product> findProductsByUser(@Param("userId") UUID userId, Pageable pageable);



}
