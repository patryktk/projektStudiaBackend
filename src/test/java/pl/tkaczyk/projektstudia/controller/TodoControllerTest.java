package pl.tkaczyk.projektstudia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.tkaczyk.projektstudia.model.Todo;
import pl.tkaczyk.projektstudia.repository.TodoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = TodoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void retrieveTodos() {
        String username = "exampleUser";

        Todo todo1 = new Todo();
        todo1.setId(1);
        todo1.setUsername(username);
        todo1.setDescription("Task 1");

        Todo todo2 = new Todo();
        todo2.setId(2);
        todo2.setUsername(username);
        todo2.setDescription("Task 2");

        List<Todo> todoList = Arrays.asList(todo1, todo2);

        when(todoRepository.findTodoByUsername(username)).thenReturn(todoList);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/users/{username}/todos", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Task 1"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Task 2"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(todoRepository, times(1)).findTodoByUsername(username);
    }

    @Test
    void retreieveTodo() {
        int todoId = 1;
        String username = "exampleUser";

        Todo todo = new Todo();
        todo.setId(todoId);
        todo.setUsername(username);
        todo.setDescription("Sample Task");

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/users/{username}/todos/{id}", username, todoId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(todoId))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Sample Task"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(todoRepository, times(1)).findById(todoId);
    }

    @Test
    void deleteTodo() {
        int todoId = 1;
        String username = "exampleUser";

        doNothing().when(todoRepository).deleteById(todoId);

        try {
            mockMvc.perform(MockMvcRequestBuilders.delete("/users/{username}/todos/{id}", username, todoId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(todoRepository, times(1)).deleteById(todoId);
    }

    @Test
    void updateTodo() {
        String username = "exampleUser";
        int todoId = 1;

        Todo existingTodo = new Todo();
        existingTodo.setId(todoId);
        existingTodo.setUsername(username);
        existingTodo.setDescription("Sample Task");

        Todo updatedTodo = new Todo();
        updatedTodo.setId(todoId);
        updatedTodo.setUsername(username);
        updatedTodo.setDescription("Updated Task");

        when(todoRepository.save(any(Todo.class))).thenReturn(updatedTodo);
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));

        try {
            mockMvc.perform(MockMvcRequestBuilders.put("/users/{username}/todos/{id}", username, todoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updatedTodo)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(todoId))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Task"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void createTodo() {
        String username = "exampleUser";

        Todo newTodo = new Todo();
        newTodo.setUsername(username);
        newTodo.setDescription("New Task");

        Todo savedTodo = new Todo();
        savedTodo.setId(1);
        savedTodo.setUsername(username);
        savedTodo.setDescription("New Task");

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/{username}/todos", username)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newTodo)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(username))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New Task"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}