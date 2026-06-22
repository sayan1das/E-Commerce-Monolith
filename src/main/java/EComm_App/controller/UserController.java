package EComm_App.controller;

import EComm_App.dto.UserRequest;
import EComm_App.dto.UserResponse;
import EComm_App.model.User;
import EComm_App.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        return userService.fetchUser(id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
        userService.addUser(userRequest);
        return ResponseEntity.ok("User Added!!");
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserRequest updatedUserRequest){
        boolean updatedOrNot = userService.updateUser(id, updatedUserRequest);
        if (updatedOrNot){
            return ResponseEntity.ok("User Updated");
        }
        return ResponseEntity.notFound().build();
    }
}


/*
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;      // With the help of @RequiredArgsConstructor I do constructor Dependency Injection

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
//        User user = userService.fetchUser(id);
//        if (user == null){                                        // <--- normal way
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(user);

        return userService.fetchUser(id)        // with java stream way
                .map(ResponseEntity::ok)        // This is method reference syntax <-- original --->  .map((user)-> ResponseEntity.ok(user))
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/users")
    public ResponseEntity<String> createUser(@RequestBody User user){
        userService.addUser(user);
        return ResponseEntity.ok("User Added!!");
    }


    @PutMapping("/api/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser){
        boolean updatedOrNot = userService.updateUser(id, updatedUser);
        if (updatedOrNot){
            return ResponseEntity.ok("User Updated");
        }
        return ResponseEntity.notFound().build();
    }
}
*/