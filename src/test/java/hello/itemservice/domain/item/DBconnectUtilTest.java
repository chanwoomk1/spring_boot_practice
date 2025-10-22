package hello.itemservice.domain.item;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import hello.itemservice.connection.DBconnectionUtil;


public class DBconnectUtilTest {
    @Test
    void connection() {
        Connection connection = DBconnectionUtil.getConnection();
        assertThat(connection).isNotNull();
    }
}
