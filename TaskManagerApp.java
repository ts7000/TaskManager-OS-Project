import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class Task {

    private String title;
    private Priority priority;
    private boolean isComplete;

    public Task(String title, Priority priority) {
        this.title = title;
        this.priority = priority;
        this.isComplete = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void markComplete() {
        this.isComplete = true;
    }

    @Override
    public String toString() {
        String status = isComplete ? "[X]" : "[ ]";
        return status + " " + priority + " - " + title;
    }
}

enum Priority {
    HIGH,
    MEDIUM,
    LOW
}

class TaskView {
    public void displayTaskList(List<Task> tasks) {
        System.out.println("Task List:");
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }
}

class TaskController {
    private List<Task> tasks;
    private TaskView taskView;

    public TaskController() {
        this.tasks = new ArrayList<>();
        this.taskView = new TaskView();
    }

    public void createTask(String title, Priority priority) {
        Task task = new Task(title, priority);
        tasks.add(task);
    }

    public void markTaskComplete(int taskIndex) {
        if (isValidTaskIndex(taskIndex)) {
            Task task = tasks.get(taskIndex - 1);
            task.markComplete();
        } else {
            System.out.println("Invalid task index.");
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    private boolean isValidTaskIndex(int taskIndex) {
        return taskIndex >= 1 && taskIndex <= tasks.size();
    }

    // Additional method to remove a task by name
    public void removeTaskByName(String taskName) {
        Task taskToRemove = null;
        for (Task task : tasks) {
            if (task.getTitle().equals(taskName)) {
                taskToRemove = task;
                break;
            }
        }
        if (taskToRemove != null) {
            tasks.remove(taskToRemove);
        }
    }
}

public class TaskManagerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskController taskController = new TaskController();
                TaskManagerGUI gui = new TaskManagerGUI(taskController);
            }
        });
    }
}

class TaskManagerGUI {
    private JFrame frame;
    private TaskController taskController;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField titleField;
    private JTextField killTaskField;
    private JComboBox<String> priorityComboBox;

    public TaskManagerGUI(TaskController taskController) {
        this.taskController = taskController;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Task Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel titleLabel = new JLabel("Task Title:");
        titleField = new JTextField(20);
        JLabel priorityLabel = new JLabel("Priority:");
        String[] priorities = {"HIGH", "MEDIUM", "LOW"};
        priorityComboBox = new JComboBox<>(priorities);
        JButton createButton = new JButton("Create Task");
        JButton killButton = new JButton("Kill Task");
        JLabel killTaskLabel = new JLabel("Task Name to Kill:");
        killTaskField = new JTextField(20);

        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(priorityLabel);
        inputPanel.add(priorityComboBox);
        inputPanel.add(createButton);
        inputPanel.add(killTaskLabel);
        inputPanel.add(killTaskField);
        inputPanel.add(killButton);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        JScrollPane taskScrollPane = new JScrollPane(taskList);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(taskScrollPane, BorderLayout.CENTER);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTask();
            }
        });

        killButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killTask();
            }
        });

        frame.setVisible(true);
    }

    private void createTask() {
        String title = titleField.getText();
        String priority = (String) priorityComboBox.getSelectedItem();

        if (!title.isEmpty()) {
            Priority taskPriority = Priority.valueOf(priority.toUpperCase());
            taskController.createTask(title, taskPriority);
            updateTaskList();
            titleField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Task title cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void killTask() {
        String taskName = killTaskField.getText();
        if (!taskName.isEmpty()) {
            taskController.removeTaskByName(taskName);
            updateTaskList();
            killTaskField.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Task name cannot be empty.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTaskList() {
        taskListModel.clear();
        for (Task task : taskController.getTasks()) {
            taskListModel.addElement(task.toString());
        }
    }
}
