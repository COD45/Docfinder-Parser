package com.sample.demo.database

import org.springframework.data.repository.CrudRepository


interface DoctorsRepository : CrudRepository<DoctorEntity, Long> {
    fun findByName(name: String): DoctorEntity?
    fun findAllByOrderByIdDesc(): Iterable<DoctorEntity>
}
