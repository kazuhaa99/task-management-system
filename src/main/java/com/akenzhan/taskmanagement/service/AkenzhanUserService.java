package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.response.AkenzhanUserResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.exception.AkenzhanResourceNotFoundException;
import com.akenzhan.taskmanagement.mapper.AkenzhanUserMapper;
import com.akenzhan.taskmanagement.repository.AkenzhanUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AkenzhanUserService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanUserService.class);

    private final AkenzhanUserRepository userRepository;
    private final AkenzhanUserMapper userMapper;

    public AkenzhanUserService(AkenzhanUserRepository userRepository, AkenzhanUserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Page<AkenzhanUserResponse> getAllUsers(Pageable pageable) {
        logger.debug("Fetching all users");
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    public AkenzhanUserResponse getUserById(Long id) {
        AkenzhanUser user = userRepository.findById(id)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public AkenzhanUserResponse getUserByUsername(String username) {
        AkenzhanUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("User not found: " + username));
        return userMapper.toResponse(user);
    }

    public AkenzhanUser getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AkenzhanResourceNotFoundException("User not found: " + username));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new AkenzhanResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User deleted: {}", id);
    }
}
