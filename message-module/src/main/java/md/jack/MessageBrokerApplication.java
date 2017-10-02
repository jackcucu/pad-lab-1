package md.jack;

import javaslang.Tuple;
import javaslang.Tuple2;
import md.jack.broker.Client;
import md.jack.dto.MessageDto;
import md.jack.model.db.Message;
import md.jack.model.db.Topic;
import md.jack.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.toMap;

@SpringBootApplication
public class MessageBrokerApplication
{
	@Autowired
	private TopicService topicService;

	@Autowired
	private Converter<Message, MessageDto> messageDtoConverter;

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
	public Map<String, Tuple2<BlockingQueue<MessageDto>, List<Client>>> topics()
	{
		return topicService.getAll().stream()
				.collect(toMap(Topic::getName, this::buildTuple));
	}

	private Tuple2<BlockingQueue<MessageDto>, List<Client>> buildTuple(final Topic topic)
	{
		final BlockingQueue<MessageDto> channel = new ArrayBlockingQueue<>(1024);

		topic.getMessages().stream()
				.filter(it -> !it.isConsumed())
				.map(messageDtoConverter::convert)
				.peek(channel::add)
				.close();

		return Tuple.of(channel, new CopyOnWriteArrayList<Client>());
	}
}
