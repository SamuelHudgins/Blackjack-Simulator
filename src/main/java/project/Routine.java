package project;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Contains interfaces and static methods for handling timed events. 
 * This class uses JavaFX's Animation class to run events after a delay or over time.
 */
public class Routine {
	
	/**
	 * An interface for implementing methods with a void return type.
	 */
	public interface Action {
		public void invoke();
	}
	
	/**
	 * An interface for implementing methods that return a boolean value.
	 */
	public interface Conditional {
		public boolean condition();
	}
	
	/**
	 * Causes the specified action(s) to occur after the specified time elapses.
	 * @param action The void method implementation to call when the time elapses.
	 * @param period The time in milliseconds to wait before calling the specified action.
	 */
	public static void doAfter(Action action, int period) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(period), event -> {
			action.invoke();
		}));
		timeline.play();
	}
	
	/**
	 * Causes the specified action(s) to repeat at the specified intervals of time until the 
	 * provided condition is true. Once the condition is evaluated to be true, the given ending 
	 * action will be executed.
	 * @param action The void method implementation to call in each iteration of the routine.
	 * @param period The time in milliseconds to wait before calling successive actions and 
	 * evaluating the end condition.
	 * @param conditional The conditional method implementation to determine when to stop the routine.
	 * @param endAction The action to execute at the end of the routine.
	 */
	public static void doRepeatOnCondition(Action action, int period, Conditional conditional, Action endAction) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(period), event -> {
			if (conditional != null && conditional.condition()) {
				timeline.stop();
				endAction.invoke();
			}
			else action.invoke();
		}));
		timeline.play();
	}
}
