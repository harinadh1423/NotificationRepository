package com.project.retailproject.clients;


import com.project.retailproject.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserApplication", url="${user.service.url}")
public interface UserClient {


    @GetMapping("/api/users/{id}")
    UserResponseDTO findById(@PathVariable Long id);


}
