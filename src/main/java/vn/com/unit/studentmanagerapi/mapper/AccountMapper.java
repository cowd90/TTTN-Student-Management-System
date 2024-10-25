package vn.com.unit.studentmanagerapi.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.com.unit.studentmanagerapi.dto.request.AccountUpdateRequest;
import vn.com.unit.studentmanagerapi.dto.response.AccountResponse;
import vn.com.unit.studentmanagerapi.entity.Account;

@Mapper(componentModel = "spring")
@DecoratedWith(AccountMapperDecorator.class)
public interface AccountMapper {
    AccountResponse toAccountResponse(Account account);
    Account updateAccount(@MappingTarget Account account, AccountUpdateRequest request);
}
