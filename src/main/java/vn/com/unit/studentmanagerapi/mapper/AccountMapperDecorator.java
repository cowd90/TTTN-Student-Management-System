package vn.com.unit.studentmanagerapi.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.entity.Account;
import vn.com.unit.studentmanagerapi.entity.enums.Role;

@Component
public abstract class AccountMapperDecorator implements AccountMapper {

    @Autowired
    private AccountMapper delegate;

    @Override
    public AccountResponse toAccountResponse(Account account) {
        AccountResponse accountResponse = delegate.toAccountResponse(account);
        String roleName = Role.fromCode(account.getRole()).name();
        accountResponse.setRole(roleName);
        return accountResponse;
    }
}
