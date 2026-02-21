package br.com.matteusmoreno.domain.repository;

import br.com.matteusmoreno.domain.entity.User;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, String> {
}
