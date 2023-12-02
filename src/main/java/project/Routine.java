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
	 * @param period The time in seconds to wait before calling the specified action.
	 */
	public static void doAfter(Action action, float period) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(period), event -> {
			action.invoke();
		}));
		timeline.play();
	}
	
	
	/**
	 * Invokes the specified start action(s) followed by the specified ending actions 
	 * after the provided period.
	 * @param startAction The void method implementation to call at the start of the routine.
	 * @param endAction The void method implementation to call at the end of the routine.
	 * @param period The time before the end action is called after the start.
	 */
	public static void onStartAndFinish(Action startAction, Action endAction, float period) {
		startAction.invoke();
		doAfter(endAction, period);
	}
	
	/**
	 * Causes the specified action(s) to repeat at the specified intervals of time until the 
	 * provided condition is true. Once the condition is evaluated to be true, the given ending 
	 * action will be executed.
	 * @param action The void method implementation to call in each iteration of the routine.
	 * @param period The time in seconds to wait before calling successive actions and 
	 * evaluating the end condition.
	 * @param conditional The conditional method implementation to determine when to stop the routine.
	 * @param endAction The action to execute at the end of the routine.
	 */
	public static void doRepeatOnCondition(Action action, float period, Conditional conditional, Action endAction) {
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(period), event -> {
			if (conditional != null && conditional.condition()) {
				timeline.stop();
				endAction.invoke();
				return;
			}
			action.invoke();
		}));
		timeline.play();
	}
	
	
	/**
	 * Invokes the specified set of actions at the specified intervals of time.
	 * @param period The time in seconds to wait before calling successive actions.
	 * @param actions The void method implementations to call in each iteration of the routine.
	 */
	public static void doActionList(float period, Action...actions) {
		Timeline timeline = new Timeline();
		float duration = period;
		for (Action action : actions) {
			timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(duration), event -> {
				action.invoke();
			}));
			duration += period;
		}		
		timeline.play();
	}
}
