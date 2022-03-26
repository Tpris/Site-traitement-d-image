package pdl.backend.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdl.backend.User.model.User;
import pdl.backend.User.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    UserRepository userRepo;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = "application/JSON")
    public @ResponseBody ResponseEntity<?> getUser(@PathVariable("id") long id){

        Optional<User> user = userRepo.findById(id);

        if (user.isPresent()) {
            userRepo.findById(user.get().getId());
            return new ResponseEntity<>("User id=" + id + " found.", HttpStatus.OK);
        }

        return new ResponseEntity<>("User id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {

        Optional<User> user = userRepo.findById(id);

        if (user.isPresent()) {
            userRepo.delete(user.get());
            return new ResponseEntity<>("User id=" + id + " deleted.", HttpStatus.OK);
        }
        return new ResponseEntity<>("User id=" + id + " not found.", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody User user) {
        userRepo.save(user);
        return new ResponseEntity<>("User added", HttpStatus.OK);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ArrayNode getUserList() {

        List<User> userList = new ArrayList<>();
        userRepo.findAll().forEach(userList::add);

        ArrayNode nodes = mapper.createArrayNode();

        for (User user : userList) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", user.getId());
            objectNode.put("username", user.getUsername());
            nodes.add(objectNode);
        }
        return nodes;
    }

}
