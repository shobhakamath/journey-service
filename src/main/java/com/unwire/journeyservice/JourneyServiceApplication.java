package com.unwire.journeyservice;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableWebMvc
@SpringBootApplication( scanBasePackages = "com.unwire.journeyservice" )
public class JourneyServiceApplication {

	//http://localhost:8080/swagger-ui/index.html#
	public static void main(String[] args) {
		SpringApplication.run(JourneyServiceApplication.class, args);
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename( "i18n/messages" );
		source.setUseCodeAsDefaultMessage( true );
		return source;
	}

	@Bean
	@Qualifier( "reactiveRedisConnectionFactory" )
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(LettuceConnectionFactory connectionFactory) {
		Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer( Object.class );

		RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext.newSerializationContext(
			new StringRedisSerializer() );

		builder
			.hashValue( new GenericJackson2JsonRedisSerializer() );
		RedisSerializationContext<String, Object> context = builder
			.hashValue( new GenericJackson2JsonRedisSerializer() )
			.hashKey( new StringRedisSerializer() )
			.key( new StringRedisSerializer() )
			.value( valueSerializer )
			.build();
		return new ReactiveRedisTemplate( connectionFactory, context );
	}


}
