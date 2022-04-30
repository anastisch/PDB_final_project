package com.pdb.project.service;

import com.pdb.project.dto.UserUpdateRequest;
import com.pdb.project.model.ERole;
import com.pdb.project.model.Role;
import com.pdb.project.model.User;
import com.pdb.project.repository.RoleRepository;
import com.pdb.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    @Value("${PDB.app.itemsOnPage}")
    private int itemsOnPage;

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public void create(@RequestBody User user) {
        if (repository.existsByUsername(user.getUsername())) {
            repository.save(user);
        } else new RuntimeException("Error: username is taken!");
    }

    public Page<User> getPage(Integer page, String username, String role) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return repository.findUserByUsernameAndRole(username, role, pageable);
    }

    public User getById(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public User update(long id, UserUpdateRequest userRequest) {
        User userToBeUpdated = getById(id);

        userToBeUpdated.setFirstName(userRequest.getFirstName());
        ;
        userToBeUpdated.setSurname(userRequest.getSurname());
        userToBeUpdated.setPatronymic(userRequest.getPatronymic());
        userToBeUpdated.setBirthDay(userRequest.getBirthDay());
        userToBeUpdated.setEmail(userRequest.getEmail());
        userToBeUpdated.setPhone(userRequest.getPhone());
        if (userRequest.getNewPassword() != null && userRequest.getNewPassword().length() > 0) {
            if (!encoder.matches(userRequest.getOldPassword(), userToBeUpdated.getPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Old password is incorrect!"
                );
            }
            userToBeUpdated.setPassword(encoder.encode(userRequest.getNewPassword()));
        }

        return repository.save(userToBeUpdated);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public void giveModeratorRights(long id) {
        User user = getById(id);
        if (!user.isAdmin() && !user.isModerator()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.valueOf("ROLE_MODERATOR")).orElseThrow());
            user.setRoles(roles);
            repository.save(user);
        }
    }

    public void giveAdminRights(long id) {
        User user = getById(id);
        if (!user.isAdmin()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.valueOf("ROLE_ADMIN")).orElseThrow());
            user.setRoles(roles);
            repository.save(user);
        }
    }
}