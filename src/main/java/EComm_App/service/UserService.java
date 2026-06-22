package EComm_App.service;

import EComm_App.dto.AddressDto;
import EComm_App.dto.UserRequest;
import EComm_App.dto.UserResponse;
import EComm_App.model.Address;
import EComm_App.model.User;
import EComm_App.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public List<UserResponse> fetchAllUsers(){
//        List<User> users = userRepository.findAll();            // get all User entities
//        List<UserResponse> responses = new ArrayList<>();       // empty list to fill manually
//
//        for (User user : users) {
//            UserResponse response = mapToUserResponse(user);    // convert one User -> one UserResponse
//            responses.add(response);                            // manually add to list
//        }
//        return responses;

        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)             // or .map(user -> this.mapToUserResponse(user))
                .collect(Collectors.toList());
    }

/*
⭐ why I need .map(user -> this.mapToUserResponse(user)) ?
My UserService class has a method mapToUserResponse. To call it, I normally need an object:
        UserService service = new UserService();
        service.mapToUserResponse(someUser);

But now I'm calling it from inside the same class, in fetchAllUsers().
Since fetchAllUsers() is also running on some UserService object, that same object is available as this.

So, "this" = current UserService object
 */
    private UserResponse mapToUserResponse(User user){
        UserResponse userResponse = new UserResponse();

        userResponse.setId(String.valueOf(user.getId()));
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());

        if (user.getAddress() != null){
            AddressDto addressDto = new AddressDto();
            addressDto.setStreet(user.getAddress().getStreet());
            addressDto.setState(user.getAddress().getState());
            addressDto.setCity(user.getAddress().getCity());
            addressDto.setCountry(user.getAddress().getCountry());
            addressDto.setZipcode(user.getAddress().getZipcode());
            userResponse.setAddressDto(addressDto);
        }
        return userResponse;
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    public void addUser( UserRequest userRequest){
        User user = new User();
        updateUserFromRequest(user, userRequest);
        userRepository.save(user);
    }
    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        if (userRequest.getAddress() != null){
            Address address = (user.getAddress() != null) ? user.getAddress() : new Address();
            address.setStreet(userRequest.getAddress().getStreet());
            address.setCity(userRequest.getAddress().getCity());
            address.setState(userRequest.getAddress().getState());
            address.setCountry(userRequest.getAddress().getCountry());
            address.setZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }


    public boolean updateUser(Long id, UserRequest updatedUserRequest){
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser, updatedUserRequest);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }
}


/*  ----------------------------------- Without DTO implementation--------------------------------------------------
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public List<User> fetchAllUsers(){
        return userRepository.findAll();            // <-- this findAll method given by JPA
    }

    public Optional<User> fetchUser(Long id) {
        return userRepository.findById(id);
    }

    public void addUser( User user){
        //System.out.println(user.getRole());
        userRepository.save(user);
    }


    public boolean updateUser(Long id, User updatedUser){
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }
}
*/

/*  ------------------------- When use List as a database---------------------------------------------------------
@Service
public class UserService {
    private final List<User> userList = new ArrayList<>();

    private Long nextId = 1L;


    public List<User> fetchAllUsers(){
        return userList;
    }


//    public User fetchUser(Long id) {
public Optional<User> fetchUser(Long id) {
//        for (User user : userList){
//            if (user.getId().equals(id)){       // here equal() and getId()-- methods made when use @Data annotation
//                return user;
//            }
//        }
//        return null;

    return userList.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst();

}

    public void addUser( User user){
        user.setId(nextId++);
        userList.add(user);
    }


    public boolean updateUser(Long id, User updatedUser){
        return userList.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .map(existingUser -> {
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setLastName(updatedUser.getLastName());
                    return true;
                }).orElse(false);
    }
}
*/