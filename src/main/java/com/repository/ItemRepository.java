package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.models.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	@Modifying
	@Transactional
	@Query("update Item i set i.ejercicio = null where i.ejercicio = :ejercicio")
	void clearEjercicioFromItems(@Param("ejercicio") com.models.Ejercicio ejercicio);
}
