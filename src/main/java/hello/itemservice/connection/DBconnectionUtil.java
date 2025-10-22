package hello.itemservice.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.itemservice.connection.ConnectionConst.PASSWORD;
import static hello.itemservice.connection.ConnectionConst.URL;
import static hello.itemservice.connection.ConnectionConst.USERNAME;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBconnectionUtil {
    public static Connection getConnection(){
        try {
            Connection connection=DriverManager.getConnection(URL,USERNAME,PASSWORD);
            log.info("get connection={}, class={}", connection,connection.getClass());
            return connection;
        } 
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }   
}
