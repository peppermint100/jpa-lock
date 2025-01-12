package com.peppermint100.jpalock

import com.peppermint100.jpalock.entity.Account
import com.peppermint100.jpalock.repository.AccountRepository
import com.peppermint100.jpalock.service.AccountService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@SpringBootTest
class AccountServiceTests {

    @Autowired private lateinit var accountService: AccountService
    @Autowired private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setup() {
        // 10000원 금액을 가지고 있는 계좌를 생성
        val account = Account(balance = 10000)
        accountRepository.save(account)
    }

    @AfterEach
    fun tearDown() {
        accountRepository.deleteAll()
    }

    /*
    남은 금액: 8000
    남은 금액: 8000
    남은 금액: 8000
    남은 금액: 8000
    남은 금액: 8000
    5개의 스레드에서 2000원 마이너스 한 값을 5번 업데이트하여 예상된 결과가 반환되지 않는다.
     */
    @Test
    fun `5개의 서로 다른 스레드에서 출금을 동시에 진행한다`() {
        println("계좌를 가져옵니다.")
        val accountId = accountRepository.findAll()[0].id!!
        val numberOfThreads = 5
        val executorService = Executors.newFixedThreadPool(32)
        val latch = CountDownLatch(numberOfThreads)

        repeat(numberOfThreads) { index ->
            executorService.submit {
                try {
                    println("계좌에서 2000원을 출금합니다.")
                    val account = accountService.withdraw(accountId, 2000)
                    println("남은 금액: ${account.getBalance()}")
                } catch (e: Exception) {
                    println("출금에 실패하였습니다.")
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()

        val finalAccount = accountRepository.findAccountById(accountId)!!

        // 2000원씩 5번 출금하므로 남은 계좌의 잔액은 0원이다.
        assertEquals(finalAccount.getBalance(), 0)
    }
}