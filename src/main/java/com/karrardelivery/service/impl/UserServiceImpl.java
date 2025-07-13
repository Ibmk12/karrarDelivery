package com.karrardelivery.service.impl;

import com.karrardelivery.dto.ChangePasswordRequest;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.dto.UserDto;
import com.karrardelivery.entity.management.User;
import com.karrardelivery.exception.ResourceNotFoundException;
import com.karrardelivery.mapper.UserMapper;
import com.karrardelivery.repository.UserRepository;
import com.karrardelivery.service.MessageService;
import com.karrardelivery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.karrardelivery.constant.Messages.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final MessageService messageService;

    @Override
    public GenericResponse<UserDto> createUser(UserDto dto) {
        if (userRepository.existsByPhoneAndDeletedFalse(dto.getPhone())) {
            throw new IllegalArgumentException(
                    messageService.getMessage("user.phone.duplicate", dto.getPhone())
            );
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEnabled(true);
        user.setDeleted(false);

        UserDto saved = userMapper.toDto(userRepository.save(user));
        return GenericResponse.successResponse(saved, messageService.getMessage(DATA_UPDATED_SUCCESSFULLY));
    }

    @Override
    public GenericResponse<UserDto> updateUser(Long id, UserDto dto) {
        User user = findUserOrThrow(id);

        userMapper.mapToUpdate(user, dto);
        UserDto updated = userMapper.toDto(userRepository.save(user));

        return GenericResponse.successResponse(updated, messageService.getMessage(DATA_UPDATED_SUCCESSFULLY));
    }

    @Override
    public GenericResponse<UserDto> getUserById(Long id) {
        UserDto userDto = userMapper.toDto(findUserOrThrow(id));
        return GenericResponse.successResponse(userDto, messageService.getMessage(DATA_FETCHED_SUCCESSFULLY));
    }

    @Override
    public GenericResponse<String> deleteUser(Long id) {
        User user = findUserOrThrow(id);
        user.setDeleted(true);
        userRepository.save(user);

        return GenericResponse.successResponseWithoutData(messageService.getMessage(DATA_DELETED_SUCCESSFULLY));
    }

    @Override
    public GenericResponse<List<UserDto>> getAllUsers() {
        List<UserDto> userDtos = userMapper.toDtoList(userRepository.findAllByDeletedFalse());
        return GenericResponse.successResponse(userDtos, messageService.getMessage(DATA_FETCHED_SUCCESSFULLY));
    }

    @Override
    public GenericResponse<String> disableUser(Long id) {
        User user = findUserOrThrow(id);
        user.setEnabled(false);
        userRepository.save(user);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> enableUser(Long id) {
        User user = findUserOrThrow(id);
        user.setEnabled(true);
        userRepository.save(user);
        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<String> changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByPhoneAndDeletedFalse(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found", request.getPhone()));

        if (!user.isEnabled()) {
            throw new IllegalStateException(messageService.getMessage("user.disabled"));
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException(messageService.getMessage("password.mismatch"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return GenericResponse.successResponseWithoutData(DATA_UPDATED_SUCCESSFULLY);
    }


    private User findUserOrThrow(Long id) {
        return userRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new IllegalArgumentException(messageService.getMessage("user.not.found", id))
        );
    }
}
