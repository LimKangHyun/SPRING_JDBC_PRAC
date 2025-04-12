package io.kanghyun.springjdbcprac.transaction;

import io.kanghyun.springjdbcprac.dao.SimpleCrudRepository;
import io.kanghyun.springjdbcprac.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@RequiredArgsConstructor
public class SimpleJdbcCrudTransactionRepository implements SimpleCrudRepository {

    // connection만 필요하기 때문에, HikariCP를 사용하더라도 DataSource(커넥션을 가지고 있음)만 가져옴
    private final DataSource dataSource;

    private Connection getConnection() throws SQLException {
        // 트랜잭션 안의 커넥션 재사용 -> 하나의 트랜잭션에서 하나의 커넥션 객체만 사용하기 위함
        return DataSourceUtils.getConnection(dataSource);
    }

    private void closeConnection(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        // 커넥션 객체 재사용을 위해 커넥션은 닫지 않음
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

    @Override
    public Member save(Member member) throws SQLException {
        String sql = "insert into member (username, password) values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int idx = rs.getInt(1);
                member.setMemberId(idx);
            }
            return member;
        } catch (SQLException e) {
            throw e;
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }
    @Override
    public Optional<Member> findById(Integer id) throws SQLException {

        String sql = "select * from where member_id = ? ";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member findMember = new Member(
                        rs.getInt("member_id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                return Optional.of(findMember);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            closeConnection(conn, pstmt, rs);
        }
    }

    @Override
    public void update(Member member) throws SQLException {
        String sql = "update member set password = ? where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, member.getPassword());
            pstmt.setInt(2, member.getMemberId());
            pstmt.executeUpdate();
        } catch (SQLException e) {

        } finally {
            closeConnection(conn, pstmt, null);
        }
    }

    @Override
    public void remove(Integer id) throws SQLException {

    }
}
