package com.example.todoappjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;

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
                return new ListCell<Task>() {
                    private final HBox hBox = new HBox();
                    private final Label label = new Label();
                    private final CheckBox checkBox = new CheckBox();
                    private final Region spacer = new Region();

                    {
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.setSpacing(10);

                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        hBox.getChildren().addAll(label, spacer, checkBox);
                    }

                    @Override
                    protected void updateItem(Task item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            label.setText(item.getTitle());
                            checkBox.setSelected(item.isDone());

                            checkBox.setOnAction(event -> {
                                boolean newState = checkBox.isSelected();
                                item.setDone(newState);
                                applyTaskStyle(newState);

                                new Thread(() -> {
                                    taskService.updateTaskStatus(item.getId(), newState);
                                }).start();
                            });

                            applyTaskStyle(item.isDone());
                            setGraphic(hBox);
                        }
                    }

                    private void applyTaskStyle(boolean isDone) {
                        if (isDone) {
                            label.setStyle("-fx-opacity: 0.5; -fx-strikethrough: true;");
                            setStyle("-fx-background-color: #f9f9f9;");
                        } else {
                            label.setStyle("-fx-opacity: 1.0; -fx-strikethrough: false;");
                            setStyle("");
                        }
                    }
                };
            }
        });
    }

    @FXML
    protected void onAddTaskClick() {
        String title = taskInputField.getText();
        if (title == null || title.trim().isEmpty()) {
            return;
        }
        taskInputField.clear();

        Task tempTask = new Task(0, title, false);
        taskItems.add(tempTask);

        new Thread(() -> {
            try {
                taskService.addTask(title);
                javafx.application.Platform.runLater(this::refreshTasks);
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    taskItems.remove(tempTask);
                    System.err.println("error add to base " + e.getMessage());
                });
            }
        }).start();
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
            int index = taskItems.indexOf(selectedTask);
            taskItems.remove(selectedTask);

            new Thread(() -> {
                try {
                    taskService.deleteTask(selectedTask.getId());
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        taskItems.add(index, selectedTask);
                    });
                }
            }).start();
        }
    }

    @FXML
    protected void onClearCompletedClick() {
        taskService.deleteCompletedTasks();
        refreshTasks();
    }
}
