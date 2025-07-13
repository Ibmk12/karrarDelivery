package com.karrardelivery.controller;

import com.karrardelivery.controller.spec.UserSpec;
import com.karrardelivery.dto.ChangePasswordRequest;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.UserDto;
import com.karrardelivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<GenericResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<UserDto>> updateUser(@PathVariable Long id,
                                                               @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<UserDto>>> getAllUsers(
            UserSpec userSpec,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(userSpec, pageable));
    }

    @PutMapping(DISABLE_USERS)
    public ResponseEntity<GenericResponse<String>> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.disableUser(id));
    }

    @PutMapping(ENABLE_USERS)
    public ResponseEntity<GenericResponse<String>> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.enableUser(id));
    }

    @PutMapping(CHANGE_PASSWORD)
    public ResponseEntity<GenericResponse<String>> changePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }

}