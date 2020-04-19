package com.tambola.game;

import com.itextpdf.text.pdf.PdfPTable;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableWebMvc
public class App extends WebMvcConfigurerAdapter {
  /*public static void main(String[] args) throws DocumentException, FileNotFoundException {
    Processor processor = new Processor();
    List<TambolaTicket> tambolaTickets = processor.generateTicket(1);
    for(TambolaTicket tambolaTicket: tambolaTickets){
      tambolaTicket.arrangeNumbers();
      tambolaTicket.printTicket();
    }


    Document document = new Document();
    PdfWriter.getInstance(document, new FileOutputStream("ticket.pdf"));

    document.open();

    PdfPTable table = new PdfPTable(COLUMN_SIZE);

    for(TambolaTicket tambolaTicket: tambolaTickets){
      tambolaTicket.addRowsInPDF(table);
    }

    document.add(table);
    document.close();

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

  private static void addRows(PdfPTable table) {
    table.addCell("row 1, col 1");
    table.addCell("row 1, col 2");
    table.addCell("row 1, col 3");
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
