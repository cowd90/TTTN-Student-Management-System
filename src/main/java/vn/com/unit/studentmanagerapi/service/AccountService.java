package vn.com.unit.studentmanagerapi.service;

import vn.com.unit.studentmanagerapi.dto.request.LoginRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountCreateRequest;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;

public interface AccountService {
    String login(LoginRequest request);
    AccountResponse createAccount(AccountCreateRequest request);
    AccountResponse updateAccount(String email, AccountUpdateRequest request);
    String deleteAccount(String email);
}
