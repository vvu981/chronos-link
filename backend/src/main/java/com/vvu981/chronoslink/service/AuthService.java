package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.dto.LoginRequest;
import com.vvu981.chronoslink.dto.UserResponseDTO;

public interface AuthService {
    UserResponseDTO login(LoginRequest loginData);
}