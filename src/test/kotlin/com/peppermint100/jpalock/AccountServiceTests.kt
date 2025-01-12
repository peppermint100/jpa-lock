package com.peppermint100.jpalock

import com.peppermint100.jpalock.entity.Account
import com.peppermint100.jpalock.repository.AccountRepository
import com.peppermint100.jpalock.service.AccountService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.OptimisticLockingFailureException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@SpringBootTest
class AccountServiceTests {

    @Autowired private lateinit var accountService: AccountService
    @Autowired private lateinit var accountRepository: AccountRepository

    @Test
    fun `경쟁 상태로 인한 잔액 오류 테스트`() {
        // 10000원이 있는 계좌 생성
        val account = accountRepository.save(Account(balance = 10000))
        val accountId = account.id!!

        // Thread 1 - 계좌에 천원 추가
        val thread1 = Thread {
            val account = accountService.deposit(accountId, 1000)
            println("Thread 1 잔액 ${account.getBalance()}")
        }

        // Thread 2 - 계좌에 2천원 추가
        val thread2 = Thread {
            val account = accountService.deposit(accountId, 2000)
            println("Thread 2 잔액 ${account.getBalance()}")
        }

        // Thread 1,2를 거의 동시에 실행하기
        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()

        // 총 금액이 13000원이어야 하지만 사실상 Thread 2만 적용된 12000원이 된다
        val updatedAccount = accountRepository.findAccountById(accountId)!!
        println("최종 잔액: ${updatedAccount.getBalance()}")
        assertEquals(13000, updatedAccount.getBalance())
    }

    @Test
    fun `낙관적락 테스트`() {
        // 10000원이 있는 계좌 생성
        val account = accountRepository.save(Account(balance = 10000))
        val accountId = account.id!!

        val thread1 = Thread {
            val account = accountService.depositWithOptimisticLock(accountId, 1000)
            println("Thread 1 잔액 ${account.getBalance()}")
        }

        val thread2 = Thread {
            val account = accountService.depositWithOptimisticLock(accountId, 1000)
            println("Thread 2 잔액 ${account.getBalance()}")
        }

        thread1.start()
        thread2.start()
        thread1.join()
        thread2.join()

        // Thread 2는 낙관적 락에 의해 롤백되고 11000원만 적용된다.
        val updatedAccount = accountRepository.findAccountById(accountId)!!
        println("최종 잔액: ${updatedAccount.getBalance()}")
        assertEquals(11000, updatedAccount.getBalance())
    }
}