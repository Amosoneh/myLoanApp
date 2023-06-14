package com.myloanapp.data.repositories;

import com.myloanapp.data.models.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<AppUser, String> {
    AppUser findUserByEmail (String email);
    AppUser findUserById (String id);
}
