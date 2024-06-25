package com.diplom.qrBackend;

import com.diplom.qrBackend.Config.FCMService;
import com.diplom.qrBackend.Models.Teacher;
import com.diplom.qrBackend.Models.TimeTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class QrBackendApplication {
	public static void main(String[] args) {
		FCMService.initializeFirebaseApp();
		SpringApplication.run(QrBackendApplication.class, args);
	}

	/**
	 * #need to specify aws  s3 access, secret key, bucket in application proferties
	 * sevice_account_key.json
	**/
}
