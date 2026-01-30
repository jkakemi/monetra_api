package com.finance.api.application.gateways;

import com.finance.api.domain.user.User;

import java.io.InputStream;
import java.util.List;

public interface UserFileGateway {
    List<User> parse(InputStream inputStream);
}
