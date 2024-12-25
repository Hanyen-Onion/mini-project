package vttp5b.ssf.miniProject1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vttp5b.ssf.miniProject1.services.CardService;
import vttp5b.ssf.miniProject1.services.FlightService;

@SpringBootApplication
public class MiniProject1Application implements CommandLineRunner {

	@Autowired
	private CardService cardSvc;

	@Override
	public void run(String...args) {
		//fSvc.getApi();
		//fSvc.getTxt();
		//cardSvc.getTextSearchApi();
	}

	public static void main(String[] args) {
		SpringApplication.run(MiniProject1Application.class, args);
	}

}
