package project;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Routine {
	
	public interface Action {
		public void invoke();
	}
	
	public interface Conditional {
		public boolean condition();
	}
	
	public static void doAfter(Action action, int period) {
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(period), event -> {
			action.invoke();
		}));
		timeline.play();
	}
	
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
