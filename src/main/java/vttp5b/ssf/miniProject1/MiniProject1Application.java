package vttp5b.ssf.miniProject1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniProject1Application implements CommandLineRunner {

	// @Autowired
	//private

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
