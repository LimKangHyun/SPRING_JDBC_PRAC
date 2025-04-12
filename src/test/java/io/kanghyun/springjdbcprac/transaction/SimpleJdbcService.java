package io.kanghyun.springjdbcprac.transaction;

import io.kanghyun.springjdbcprac.dao.SimpleCrudRepository;
import io.kanghyun.springjdbcprac.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SimpleJdbcService {

    private final SimpleCrudRepository repository;
    private final PlatformTransactionManager transactionManager;

    // 트랜잭션을 마무리할 때, 커밋하는 메서드 -> 데이터베이스에 반영 X
    public void logic1() {

        // 트랜잭션 시작점
        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition()
        );
        try {
            Member saveReq = new Member();
            Member saved = repository.save(saveReq);
            saved.setPassword("123456");

            repository.update(saved);
            transactionManager.commit(transaction);
        } catch (SQLException e) {
            transactionManager.rollback(transaction);
        }
    }

    // 트랜잭션을 마무리할때, 롤백하는 메서드 -> 데이터베이스에 반영 X
    public void logic2(Member saveReq, boolean isRollback) {

        TransactionStatus transaction = transactionManager.getTransaction(
                new DefaultTransactionDefinition()
        );
        try {
            Member saved = repository.save(saveReq);
            Optional<Member> memberOptional = repository.findById(saved.getMemberId());
            Member findMember = memberOptional.orElseThrow();

            log.info("Member.getUsername() = {}", findMember.getUsername());

            if (isRollback) {
                transactionManager.rollback(transaction);
                return;
            }
            transactionManager.rollback(transaction);
        } catch (SQLException e) {
            transactionManager.rollback(transaction);
        }
    }
}