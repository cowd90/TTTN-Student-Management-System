package vn.com.unit.studentmanagerapi.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import vn.com.unit.studentmanagerapi.entity.Account;

import java.time.LocalDateTime;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, String> {
    @Modifying
    @Query("INSERT INTO common_account (email, password, role, created_at, update_at, update_by, is_deleted) " +
            "VALUES (:email, :password, :role, :createdAt, :updateAt, :updateBy, :isDeleted)")
    boolean insertUser(String email, String password, Character role,
                    LocalDateTime createdAt, LocalDateTime updateAt, String updateBy, boolean isDeleted);

    @Modifying
    @Query("UPDATE common_account SET is_deleted = TRUE WHERE email = :email")
    boolean deleteAccountByEmail(String email);

    boolean existsByEmail(String email);

}
