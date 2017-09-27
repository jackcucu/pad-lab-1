package md.jack;

import javaslang.Tuple2;
import md.jack.broker.Client;
import md.jack.broker.Server;
import md.jack.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class MessageBrokerApplication
{
	public static void main(String[] args) {
		SpringApplication.run(MessageBrokerApplication.class, args);
	}

	@Bean
	public TaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(50);
		pool.setMaxPoolSize(100);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}

	@Bean
	public Map<String, Tuple2<BlockingQueue<Message>, List<Client>>> topics()
	{
		return new HashMap<>();
	}
}
