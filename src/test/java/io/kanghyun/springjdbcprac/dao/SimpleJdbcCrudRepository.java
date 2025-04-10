package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;

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

    // jdbc로 회원가입
    @Override
    public Member save(Member member) throws SQLException{
        String sql = "insert into member (username, password) values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();

            if (rs.next()) {
                // rs의 첫번째 칼럼의 pk 즉 id값
                int idx = rs.getInt(1);
            }
            return member;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeConnection(conn, pstmt, rs);
        }
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
