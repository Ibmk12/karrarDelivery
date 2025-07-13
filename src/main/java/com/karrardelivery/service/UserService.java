package com.karrardelivery.service;


import com.karrardelivery.dto.ChangePasswordRequest;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.UserDto;

import java.util.List;

public interface UserService {
    GenericResponse<UserDto> createUser(UserDto userDto);
    GenericResponse<UserDto> updateUser(Long id, UserDto userDto);
    GenericResponse<UserDto> getUserById(Long id);
    GenericResponse<String> deleteUser(Long id);
    GenericResponse<List<UserDto>> getAllUsers();
    GenericResponse<String> disableUser(Long id);
    GenericResponse<String> enableUser(Long id);
    GenericResponse<String> changePassword(ChangePasswordRequest request);
}
