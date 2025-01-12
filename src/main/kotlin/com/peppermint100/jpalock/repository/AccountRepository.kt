package com.peppermint100.jpalock.repository

import com.peppermint100.jpalock.entity.Account
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface AccountRepository: JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.id=:id")
    fun findAccountById(id: Long): Account?

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT a FROM Account a WHERE a.id=:id")
    fun findAccountByIdWithOptimisticLock(id: Long): Account?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id=:id")
    fun findAccountByIdWithPessimisticLock(id: Long): Account?
}