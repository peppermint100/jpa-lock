package com.peppermint100.jpalock.service

import com.peppermint100.jpalock.entity.Account
import com.peppermint100.jpalock.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
        private val accountRepository: AccountRepository
) {

    @Transactional(readOnly = true)
    fun getBalance(accountId: Long): Account {
        return accountRepository.findAccountById(accountId) ?: throw IllegalStateException("존재하지 않는 계좌입니다.")
    }

    @Transactional
    fun deposit(accountId: Long, balance: Int): Account {
        val account = getBalance(accountId)
        account.updateBalance(balance)
        return account
    }

    @Transactional
    fun withdraw(accountId: Long, balance: Int): Account {
        val account = getBalance(accountId)

        if (!account.isDrawable(balance)) {
            throw IllegalStateException("계좌의 잔액이 모자랍니다. 현재 잔액 ${account.getBalance()}, 출금하려는 금액 $balance")
        }

        account.updateBalance(-balance)

        return account
    }
}