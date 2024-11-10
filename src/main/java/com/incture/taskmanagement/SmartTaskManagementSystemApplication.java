package com.incture.taskmanagement;

import com.incture.taskmanagement.config.AppConstants;
import com.incture.taskmanagement.entities.Role;
import com.incture.taskmanagement.repositories.RoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class SmartTaskManagementSystemApplication implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;


	public static void main(String[] args) {
		SpringApplication.run(SmartTaskManagementSystemApplication.class, args);
	}


	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}


	@Override
	public void run(String... args) throws Exception {

//		System.out.println("Alok@123 ->"+this.passwordEncoder.encode("Alok@123"));
//		System.out.println("Vishal@123 ->"+this.passwordEncoder.encode("Vishal@123"));

		try {
			Role role1 = new Role();
			role1.setId(Long.valueOf(AppConstants.ADMIN_USER));
			role1.setName("ADMIN_USER");

			Role role2 = new Role();
			role2.setId(Long.valueOf(AppConstants.NORMAL_USER));
			role2.setName("NORMAL_USER");

			List<Role> roles = List.of(role1,role2);
			List<Role> resultList = this.roleRepo.saveAll(roles);
			resultList.forEach(r-> {
				System.out.println(r.getName());
			});

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
