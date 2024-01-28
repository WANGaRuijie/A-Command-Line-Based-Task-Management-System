package hk.edu.polyu.comp.comp2021.tms.model.instructions;

import hk.edu.polyu.comp.comp2021.tms.model.*;
import hk.edu.polyu.comp.comp2021.tms.model.instructions.DeleteTaskExecutor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeleteTaskExecutorTest {

    private TaskRecorder taskRecorder;
    private HistoryCommandRecorder commandRecorder;
    private CriterionRecorder criterionRecorder;
    private Task task1;
    private Task task2;

    @Before
    public void setUp() {
        taskRecorder = new TaskRecorder();
        commandRecorder = new HistoryCommandRecorder();
        criterionRecorder = new CriterionRecorder();

        // Create tasks and add them to the recorder
        task1 = new Task("Task1", "Task1 Description", 1.0, new Task[]{}, new Task[]{}, true);
        task2 = new Task("Task2", "Task2 Description", 2.0, new Task[]{task1}, new Task[]{}, true); // Task1 is a prerequisite of Task2

        taskRecorder.addTask(task1);
        taskRecorder.addTask(task2);
    }

    @Test
    public void testExecuteInstruction() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        // Test with valid parameters
        String[] parameters = new String[]{"Task1"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertTrue(executor.getStatus());

        // Test with invalid parameters
        parameters = new String[]{"NonexistentTask"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertTrue(executor.getStatus());
    }



    @Test
    public void testExecuteInstructionDifferentParameters() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        String[] parameters = new String[]{"Task1", "Task2"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertFalse(executor.getStatus());  // getStatus should return false because the parameters are invalid
    }

    @Test
    public void testExecuteInstructionDeletingPrerequisite() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        String[] parameters = new String[]{"Task1"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertFalse(executor.getStatus());

        assertEquals(2, taskRecorder.getAllTasks().length); // Both tasks should still exist as the delete was unsuccessful
    }

    @Test
    public void testExecuteInstructionDeletingNonExistentTask() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        String[] parameters = new String[]{"NonExistentTask"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertFalse(executor.getStatus());
    }



    @Test
    public void testUndoInstruction() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        // Execute an instruction
        String[] parameters = new String[]{"Task1"};
        executor.executeInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);
        assertTrue(executor.getStatus());

        // Undo the instruction
        executor.undoInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);

        // Check if the task is restored
        assertTrue(taskRecorder.existsTask("Task1"));
        assertTrue(taskRecorder.existsTask("Task2"));
    }

    @Test
    public void testUndoInstructionWithoutExecution() {
        DeleteTaskExecutor executor = new DeleteTaskExecutor();

        // Attempt to undo an instruction without executing an instruction first
        String[] parameters = new String[]{"NonExistentTask"};
        executor.undoInstruction(parameters, taskRecorder, commandRecorder, criterionRecorder);

        assertTrue(taskRecorder.existsTask("Task1"));
        assertTrue(taskRecorder.existsTask("Task2"));
    }

}
