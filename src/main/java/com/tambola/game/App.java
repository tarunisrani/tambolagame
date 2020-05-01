package com.tambola.game;

import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableWebMvc
@ComponentScan(lazyInit = true)
public class App extends WebMvcConfigurerAdapter {
  /*public static void main(String[] args) {
    TicketService processor = new TicketService();
    List<TambolaTicket> tambolaTickets = processor.generateTicket(1);
    for(TambolaTicket tambolaTicket: tambolaTickets){
      tambolaTicket.arrangeNumbers();
      tambolaTicket.printTicket();
    }
  }*/

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(App.class);
    application.run(args);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new GsonHttpMessageConverter());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*");
  }


  /*private static void addTableHeader(PdfPTable table) {
    Stream.of("column header 1", "column header 2", "column header 3")
        .forEach(columnTitle -> {
          PdfPCell header = new PdfPCell();
          header.setBackgroundColor(BaseColor.LIGHT_GRAY);
          header.setBorderWidth(2);
          header.setPhrase(new Phrase(columnTitle));
          table.addCell(header);
        });
  }*/
}
