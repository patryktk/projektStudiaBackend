package pl.tkaczyk.projektstudia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.tkaczyk.projektstudia.model.Todo;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findTodoByUsername(String username);
}
