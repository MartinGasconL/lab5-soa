package soa.eip;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {

  public static final String DIRECT_URI = "direct:twitter";

  @Override
  public void configure() {
    from(DIRECT_URI)
            .log("Body contains \"${body}\"")
            .log("Searching twitter for \"${body}\"!")
            .process(new MaxProcessor())
            .toD("twitter-search:${body}")
            .log("Body now contains the response from twitter:\n${body}");
  }

  private static class MaxProcessor implements Processor {
    public void process(Exchange exchange) {
      String message = exchange.getIn().getBody(String.class);
      String[] words = message.split(" ");
      StringBuilder body = new StringBuilder();
      String max = "";
      for(String word : words){
        if(word.matches("max:[0-9]+")){
          max = word.substring(4);
        }else {
            body.append(" ").append(word);
        }
      }
      if(!max.equals("")){
        body.append("?count=").append(max);
      }

      exchange.getOut().setBody(body);
    }
  }
}
