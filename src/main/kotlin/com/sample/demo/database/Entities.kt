package com.sample.demo.database

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class DoctorEntity (
    val name: String,
    val fow: String, //Field of work
    val phone: String,
    val insurances: String,
    val languages: String,
    val zip: String,
    @Id @GeneratedValue var id: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DoctorEntity) return false

        if (name != other.name) return false
        if (fow != other.fow) return false
        if (zip != other.zip) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + fow.hashCode()
        result = 31 * result + zip.hashCode()
        return result
    }
}
