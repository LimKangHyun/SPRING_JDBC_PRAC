package io.kanghyun.springjdbcprac.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ConnectionUtilTest {

    @Test
    @DisplayName("DatabaseConnection 테스트")
    void connection_test() throws Exception {

        Connection conn = ConnectionUtil.getConnection();
        log.info("conn = {}", conn);
        conn.close();

    }

}