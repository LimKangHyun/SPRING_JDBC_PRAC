package io.kanghyun.springjdbcprac.transaction;

import io.kanghyun.springjdbcprac.dao.SimpleCrudRepository;
import io.kanghyun.springjdbcprac.member.Member;
import io.kanghyun.springjdbcprac.util.ConnectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.junit.jupiter.api.Assertions.*;

class SimpleJdbcServiceTest {

    SimpleCrudRepository repository;
    SimpleJdbcService simpleJdbcService;

    // DB 관련 작업 트랜잭션으로 묶어서 처리
    @BeforeEach
    void init() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource(
                ConnectionUtil.MysqlDbConnectionConstant.URL,
                ConnectionUtil.MysqlDbConnectionConstant.USERNAME,
                ConnectionUtil.MysqlDbConnectionConstant.PASSWORD
        );
        repository = new SimpleJdbcCrudTransactionRepository(dataSource);
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        simpleJdbcService = new SimpleJdbcService(repository, transactionManager);
    }

    @Test
    @DisplayName("정상 커밋 테스트")
    void commit() throws Exception {

        Member saveReq1 = new Member(0, "member100", "member100");
        Member saveReq2 = new Member(0, "member200", "member200");

        simpleJdbcService.logic(saveReq1, false);
        simpleJdbcService.logic(saveReq2, false);
        
    }
    
    @Test
    @DisplayName("롤백 테스트")
    void rollback() throws Exception {

        Member saveReq1 = new Member(0, "member500", "member500");
        Member saveReq2 = new Member(0, "member600", "member600");

        simpleJdbcService.logic(saveReq1, true);
        simpleJdbcService.logic(saveReq2, true);

    }
}