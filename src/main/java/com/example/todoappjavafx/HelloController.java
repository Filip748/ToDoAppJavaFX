package com.example.todoappjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.util.Callback;

public class HelloController {

    @FXML
    private TextField taskInputField;

    @FXML
    private ListView<Task> taskListView;

    private TaskService taskService;
    private ObservableList<Task> taskItems;

    @FXML
    public void initialize() {
        this.taskService = new TaskService();
        this.taskItems = FXCollections.observableArrayList();

        taskListView.setItems(taskItems);

        setUpListView();
        refreshTasks();
    }

    private void setUpListView() {
        taskListView.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
           @Override
           public ListCell<Task> call(ListView<Task> param) {
               return new ListCell<>() {
                   private final CheckBox checkBox = new CheckBox();

                   @Override
                   protected void updateItem(Task item, boolean empty) {
                       super.updateItem(item, empty);

                       if (empty || item == null) {
                           setGraphic(null);
                           setText(null);
                           setStyle("");
                       } else {
                           checkBox.setText(item.getTitle());
                           checkBox.setSelected(item.isDone());

                           checkBox.setOnAction(event -> {
                               item.setDone(checkBox.isSelected());
                               taskService.updateTaskStatus(item.getId(), item.isDone());
                               applyTaskStyle(item.isDone());
                           });

                           applyTaskStyle(item.isDone());
                           setGraphic(checkBox);
                       }
                   }

                   private void applyTaskStyle(boolean isDone) {
                       if (isDone) {
                           setStyle("-fx-opacity: 0.5;");
                       } else {
                           setStyle("-fx-opacity: 1.0;");
                       }
                   }
               };
           }
        });
    }

    @FXML
    protected void onAddTaskClick() {
        String title = taskInputField.getText();
        if (title != null && !title.trim().isEmpty()) {
            taskInputField.clear();

            new Thread(() -> {
                taskService.addTask(title);
                refreshTasks();
            }).start();
        }
    }

    @FXML
    protected void refreshTasks() {

        new Thread(() -> {
            var tasks = taskService.getAllTasks();
            javafx.application.Platform.runLater(() -> {
                taskItems.clear();
                taskItems.addAll(tasks);
            });
        }).start();
    }

    @FXML
    protected void onDeleteTaskClick() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskService.deleteTask(selectedTask.getId());
            refreshTasks();
        }
    }

    @FXML
    protected void onClearCompletedClick() {
        taskService.deleteCompletedTasks();
        refreshTasks();
    }
}
