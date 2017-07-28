package pw.crutchtools.hisau.app;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableJpaRepositories(basePackageClasses = pw.crutchtools.hisau.component.repo.Repo.class)
public class WebMvcConfig extends WebMvcConfigurerAdapter{

	@Bean
	public FreeMarkerViewResolver viewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setCache(true);
		viewResolver.setPrefix("");
		viewResolver.setSuffix(".ftl");
		return viewResolver;
	}

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("/WEB-INF/view");
		Properties props = new Properties();
		props.setProperty("tag_syntax", "square_bracket");
		props.setProperty("default_encoding", "utf-8");
		props.setProperty("template_exception_handler", "rethrow");
		configurer.setFreemarkerSettings(props);
		return configurer;
	}
	
	//prevent conflict appcontext DataSource and server-context
	@Bean
	public AnnotationMBeanExporter annotationMBeanExporter() {
	    AnnotationMBeanExporter annotationMBeanExporter = new AnnotationMBeanExporter();
	    annotationMBeanExporter.addExcludedBean("dataSource");
	    annotationMBeanExporter.setRegistrationPolicy(RegistrationPolicy.IGNORE_EXISTING);
	    return annotationMBeanExporter;
	}
	
	@Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    } 
	
}
