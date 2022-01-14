package id.co.asyst.amala.getpromobycode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
@ImportResource( {"redeempromo-transaction-getpromobycode.xml", "beans.xml"} )
//@ImportResource( {"example.xml", "beans.xml"} )
public class Application {

    public static void main(String[] args) throws Exception{
    	
        SpringApplication.run(Application.class, args);
    }
}