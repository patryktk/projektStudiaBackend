package pl.tkaczyk.projektstudia.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.tkaczyk.projektstudia.model.Todo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@EntityScan("pl.tkaczyk.projektstudia.model")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoRepositoryTest {


    @Autowired
    private TodoRepository todoRepository;


    @Test
    public void todoRepository_save() {
        Todo savedTodo = new Todo("username", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        Assertions.assertThat(savedTodo).isNotNull();
        Assertions.assertThat(savedTodo.getId()).isGreaterThan(0);
    }

    @Test
    public void todoRepository_getAll() {
        Todo savedTodo = new Todo("username2", "description", LocalDate.now(), false);
        Todo savedTodo2 = new Todo("username3", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        todoRepository.save(savedTodo2);

        List<Todo> todoList = todoRepository.findAll();

        Assertions.assertThat(todoList).isNotNull();
        Assertions.assertThat(todoList.size()).isEqualTo(2);
    }

    @Test
    public void todoRepository_findTodoByUsername() {
        Todo savedTodo = new Todo("username2", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        List<Todo> listTodoFindByUsername = todoRepository.findTodoByUsername(savedTodo.getUsername());

        Assertions.assertThat(listTodoFindByUsername).isNotNull();
    }

    @Test
    public void todoRepository_findTodoById() {
        Todo savedTodo = new Todo("username2", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        Todo todo = todoRepository.findById(savedTodo.getId()).get();

        Assertions.assertThat(todo).isNotNull();
    }

    @Test
    public void todoRepository_updateTodo() {
        Todo savedTodo = new Todo("username2", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        Todo todo = todoRepository.findById(savedTodo.getId()).get();
        todo.setDescription("descriptionUpdated");

        Todo todoUpdated = todoRepository.save(todo);

        Assertions.assertThat(todoUpdated.getDescription()).isNotNull();
        Assertions.assertThat(todoUpdated.getDescription()).isEqualTo("descriptionUpdated");
    }

    @Test
    public void todoRepository_deleteTodo() {
        Todo savedTodo = new Todo("username2", "description", LocalDate.now(), false);
        todoRepository.save(savedTodo);
        todoRepository.deleteById(savedTodo.getId());

        Optional<Todo> todo = todoRepository.findById(savedTodo.getId());


        Assertions.assertThat(todo).isEmpty();
    }

}