package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getUsers(List<Integer> ids, int from, int size) {
        if (ids != null && !ids.isEmpty()) {
            List<User> users = userRepository.findUsersByIdIn(ids);

            return users.stream()
                    .map(UserMapper::mapToUserDto)
                    .toList();
        } else {
            Pageable pageable = PageRequest.of(from / size, size);
            Page<User> users = userRepository.findUserBy(pageable);

            return users.stream()
                    .map(UserMapper::mapToUserDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest user) {
        User newUser = userRepository.save(UserMapper.mapToUser(user));
        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
}
