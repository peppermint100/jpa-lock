package com.peppermint100.jpalock.service

import com.peppermint100.jpalock.entity.Account
import com.peppermint100.jpalock.repository.AccountRepository
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
        private val accountRepository: AccountRepository
) {

    @Transactional(readOnly = true)
    fun getBalance(accountId: Long): Account {
        return accountRepository.findAccountById(accountId) ?: throw IllegalStateException("존재하지 않는 계좌입니다.")
    }

    @Transactional(readOnly = true)
    fun getBalanceWithOptimisticLock(accountId: Long): Account {
        return accountRepository.findAccountByIdWithOptimisticLock(accountId) ?: throw IllegalStateException("존재하지 않는 계좌입니다.")
    }

    @Transactional
    fun deposit(accountId: Long, balance: Int): Account {
        val account = getBalance(accountId)
        account.updateBalance(balance)
        return account
    }

    @Transactional
    fun depositWithOptimisticLock(accountId: Long, balance: Int, retryCount: Int = 3): Account {
        val account = getBalanceWithOptimisticLock(accountId)
        account.updateBalance(balance)
        return account
    }

    @Transactional
    fun depositWithPessimisticLock(accountId: Long, amount: Int): Account {
        val account = accountRepository.findAccountByIdWithPessimisticLock(accountId) ?: throw IllegalStateException("존재하지 않는 계좌입니다.")
        account.updateBalance(amount)
        return account
    }
}