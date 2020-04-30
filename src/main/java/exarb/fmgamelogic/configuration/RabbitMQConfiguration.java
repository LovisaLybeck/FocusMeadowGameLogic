package exarb.fmgamelogic.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
public class RabbitMQConfiguration implements RabbitListenerConfigurer {

    /**
     * Creates a topic exchange bean.
     * @return TopicExchange
     */
    @Bean
    public TopicExchange timerDoneExchange(@Value("{timer.exchange}") final String exchangeName) {
        return new TopicExchange(exchangeName);
    }

    /**
     * The default RabbitTemplate is overridden and replaced so
     * that it uses the JSON message converter
     * @param connectionFactory
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    /**
     * Serializes a java object to JSON
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Creates a topic exchange bean.
     * @return TopicExchange
     */
    @Bean
    public TopicExchange userExchange(@Value("{user.exchange}") final String exchangeName) {
        return new TopicExchange(exchangeName);
    }

    /**
     * Creates a durable Queue for timerCount events.
     * @param queueName
     * @return
     */
    @Bean
    public Queue userQueue (@Value("${user.queue}") final String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * Binds the topic exchange and the queue together.
     * @param queue
     * @param userExchange
     * @param routingKey
     * @return
     */
    @Bean
    Binding binding(final Queue queue, final TopicExchange userExchange,
                    @Value("${user.anything.routing-key}") final String routingKey) {
        return BindingBuilder.bind(queue).to(userExchange).with(routingKey);
    }

    /**
     * Creating a converter from JSON
     * @return
     */
    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    /**
     * Method that customizes how the message payload will be converted from serialized to a typed object.
     * @return
     */
    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    /**
     * Registering a RabbitListenerEndpoint that will use a customized MessageHandlerMethodFactory
     * @param registrar
     */
    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }
}
