package io.kanghyun.springjdbcprac.dao;

import io.kanghyun.springjdbcprac.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

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

    // 조회값이 null일수도 있으므로 Optional 처리
    @Override
    public Optional<Member> findById(Integer id) throws SQLException {
        String sql = "select * from member where member_id = ?";

        // getConnection이 실패할경우 프로그램이 터지므로, null로 초기화하여 자원을 안전하게 관리
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            // ?의 첫번째 자리에 매개변수 넣어주기
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // rs 데이터셋에서 읽어온 값을 findMember에 넣어주고 Optional로 안전하게 반환
                // SQL 의존성 끊어지는 구간
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
    public void update() {

    }

    @Override
    public void delete() {

    }
}
