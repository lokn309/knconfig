package cn.lokn.knconfig.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: distributed lock
 * @author: lokn
 * @date: 2024/06/10 22:05
 */
@Component
public class DistributedLocks {

    @Autowired
    DataSource dataSource;

    Connection connection;

    @Getter
    private AtomicBoolean locked = new AtomicBoolean(false);

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        executorService.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public boolean lock() throws SQLException {
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        connection.createStatement().execute("set innodb_lock_wait_timeout=5");
        // lock 5s
        connection.createStatement().execute("select app from locks where id = 1 for update");

        if (locked.get()) {
            System.out.println(" ===>> reenter this dist lock.");
        } else {
            System.out.println(" ===>> get a dist lock.");
        }
        return true;
    }

    private void tryLock() {
        try {
            lock();
            locked.set(true);
        } catch (Exception e) {
            System.out.println(" lock failed...");
            locked.set(false);
        }
    }

    @PreDestroy
    protected void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
                connection.close();
            }
        } catch (Exception e) {
            System.out.println(" ignore this close exception");
        }
    }
}