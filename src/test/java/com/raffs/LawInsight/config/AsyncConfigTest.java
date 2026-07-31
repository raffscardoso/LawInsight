package com.raffs.LawInsight.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AsyncConfigTest {

    @Autowired
    private Executor taskExecutor;

    @Test
    void shouldConfigureThreadPoolTaskExecutorBean() {
        assertThat(taskExecutor).isNotNull();
        assertThat(taskExecutor).isInstanceOf(ThreadPoolTaskExecutor.class);

        ThreadPoolTaskExecutor pool = (ThreadPoolTaskExecutor) taskExecutor;
        assertThat(pool.getCorePoolSize()).isEqualTo(5);
        assertThat(pool.getMaxPoolSize()).isEqualTo(10);
        assertThat(pool.getThreadNamePrefix()).isEqualTo("ContractAsync-");
    }
}
