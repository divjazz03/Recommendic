package com.divjazz.recommendic.user.controller.admin;


import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Gender;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.service.AdminService;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")

public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("create")
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody AdminRequestParams requestParams){
        AdminDTO adminDTO = new AdminDTO(
                new UserName(requestParams.firstName(), requestParams.lastName()), requestParams.email(), requestParams.phoneNumber(),
                switch (requestParams.gender().toUpperCase()){
                    case "MALE" -> Gender.MALE;
                    case "FEMALE" -> Gender.FEMALE;
                    default -> throw new IllegalArgumentException("No such Gender");
                },
                new Address(requestParams.zipcode(), requestParams.city(), requestParams.state(), requestParams.country())
        );
        return adminService.createAdmin(adminDTO);
    }

    @GetMapping("admins")
    public ResponseEntity<Set<User>> getAdmins(){
        return adminService.getAllAdmins();
    }
}
