package com.project.KoiBookingSystem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.scheduling.annotation.EnableScheduling;
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "UserAPI", version = "1.0", description = "Information"))
@SecurityScheme(name = "api", scheme = "Bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
<<<<<<< HEAD
@EnableScheduling
=======
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
public class KoiBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(KoiBookingSystemApplication.class, args);
	}

}
