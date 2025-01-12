package com.peppermint100.jpalock.repository

import com.peppermint100.jpalock.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AccountRepository: JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.id=:id")
    fun findAccountById(id: Long): Account?
}