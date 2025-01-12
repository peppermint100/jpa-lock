package com.peppermint100.jpalock.entity

import jakarta.persistence.*

@Entity
@Table(name = "accounts")
class Account(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @Column(name = "balance", nullable = false)
        private var balance: Int = 0
) {

    fun updateBalance(balance: Int) {
        this.balance += balance
    }

    fun getBalance():Int {
        return balance
    }

    fun isDrawable(balance: Int): Boolean {
        return this.balance - balance > 0
    }
}