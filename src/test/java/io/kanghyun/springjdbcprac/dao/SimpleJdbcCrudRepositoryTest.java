package io.kanghyun.springjdbcprac.dao;

import com.zaxxer.hikari.HikariDataSource;
import io.kanghyun.springjdbcprac.member.Member;
import io.kanghyun.springjdbcprac.util.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class SimpleJdbcCrudRepositoryTest {

    // 구현체보다 인터페이스를 통해 테스트를 하는것이 좋다.
    // 상위타입을 통한 테스트가 좋음 -> 구현이 아닌 역할(기능)에 의존하는 것이 이점이 많다.
    SimpleCrudRepository repository;

    @BeforeEach
    void init() {
        // Hikari CP는
        // 자바에서 DB랑 연결할 때 Connection을 직접 만들고 닫고 하면
        //→ 성능 저하 + 리소스 낭비가 크기 때문에
        //커넥션 풀(Pool)을 사용해서 미리 일정 수의 연결을 만들어두고 재사용하는 거야.
        // HikariDataSource는 Hikari CP의 구현체
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(ConnectionUtil.MysqlDbConnectionConstant.URL);
        dataSource.setUsername(ConnectionUtil.MysqlDbConnectionConstant.USERNAME);
        dataSource.setPassword(ConnectionUtil.MysqlDbConnectionConstant.PASSWORD);

        repository = new SimpleJdbcCrudRepository(dataSource);
    }

    @Test
    @DisplayName("save test")
    void save_test() throws Exception {

        String randomUserStr = "USER_" + (int) (Math.random() * 100_000);
        String randomUserPwd = randomUserStr + "_PWD";
        log.info(randomUserStr);

        //회원가입 요청
        // member_id는 0으로 초기화만 진행 -> 실제로는 auto_increment의 숫자가 매핑되어 db에 적용됨
        Member saveReq = new Member(0, randomUserStr, randomUserPwd);
        Member savedMember = repository.save(saveReq);

        log.info("saved member : {}", savedMember);
        assertThat(savedMember.getMemberId()).isEqualTo(0);

    }

    @Test
    @DisplayName("read test")
    void read_test_success() throws Exception {

        int availableIdx = 1;

        Optional<Member> memberOptional = repository.findById(availableIdx);
        boolean result = memberOptional.isPresent();
        assertThat(result).isTrue();

        Member findMember = memberOptional.get();

        assertThat(findMember).isNotNull();
        assertThat(findMember.getMemberId()).isEqualTo(availableIdx);

        log.info("find member : {}", findMember);

    }

    @Test
    @DisplayName("read test")
    void read_test_fail() throws Exception {

        int inAvailableIdx = 9999;

        Optional<Member> memberOptional = repository.findById(inAvailableIdx);
        boolean result = memberOptional.isPresent();
        assertThat(result).isFalse();

        // 검증내용 : memberOptional.get()을 했을 때, NoSuchElementException이 터졌는지 확인
        assertThatThrownBy(
                () -> {
                    memberOptional.get();
                }
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("update test")
    void update_test() throws Exception {
    
        int availableIdx = 1;

        Optional<Member> memberOptional = repository.findById(availableIdx);
        boolean result = memberOptional.isPresent();
        assertThat(result).isTrue();

        Member findMember = memberOptional.get();

        String targetPwd = UUID.randomUUID().toString();

        findMember.setPassword(targetPwd);
        // 변경한 비번 업데이트
        repository.update(findMember);

        // 검증 내용 : 업데이트한 멤버객체가 정상적으로 수행되었는지 확인
        Optional<Member> findMemberOptional = repository.findById(availableIdx);
        boolean result2 = findMemberOptional.isPresent();
        assertThat(result2).isTrue();

        // 검증 내용 : 변경한 비번이 멤버객체에 잘 들어갔는지 확인 + 업데이트 전 멤버 객체와 각 요소 비교(findMember)
        Member updatedMember = findMemberOptional.get();
        assertThat(updatedMember.getPassword()).isEqualTo(findMember.getPassword());
        assertThat(updatedMember.getMemberId()).isEqualTo(findMember.getMemberId());
        assertThat(updatedMember.getUsername()).isEqualTo(findMember.getUsername());
        log.info("update member : {}", updatedMember);
    }
}