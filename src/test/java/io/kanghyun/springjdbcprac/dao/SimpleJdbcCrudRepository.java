package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@RequiredArgsConstructor
public class SimpleJdbcCrudRepository implements SimpleCrudRepository {

    // 커넥션 풀 사용
    private final DataSource dataSource;

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void closeConnection(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(connection);
    }
    @Override
    public void save(Member member) {

    }

    @Override
    public void findById() {

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
