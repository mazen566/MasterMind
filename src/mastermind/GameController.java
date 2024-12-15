package mastermind;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class GameController {
    private static final int codeLength = 4;
    private static final int maxAttempts = 15;
    private int attempts;
    private List<Color> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.GRAY);
    private List<Color> secretCode;
    private Color[] guesses = new Color[codeLength];
    private VBox layout;
    private GridPane guessGrid;
    private int seconds = 0;
    private int minutes = 0;
    private Timeline time;
    private Label feedbackLabel;
    private Label timeLabel;
    private Label resultLabel;
    public GameController() {
        attempts = 0;
        generateSecretCode();
    }
    public VBox createLayout() {
        layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        timeLabel = new Label("00:00");
        timeLabel.setId("timeLabel");
        resultLabel = new Label();
        resultLabel.setFont(new javafx.scene.text.Font("Arial", 16));
        resultLabel.setTextFill(Color.WHITE);
        Label titleLabel = new Label("Welcome to Mastermind Game");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 24));
        titleLabel.setTextFill(Color.WHITE);
        Label instructionsLabel = new Label("Guess the secret code by select 4 colors.\nYou have 15 attempts.");
        instructionsLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        instructionsLabel.setTextFill(Color.WHITE);
        guessGrid = new GridPane();
        guessGrid.setHgap(15);
        guessGrid.setVgap(15);
        guessGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < codeLength; i++) {
            Button colorButton = new Button("Pick Color");
            colorButton.setPrefSize(120, 50);
            colorButton.setId("colorButton");
            int index = i;
            colorButton.setOnAction(e -> pickColor(colorButton, index));
            guessGrid.add(colorButton, i, 0);
        }
        Button submitButton = new Button("Submit");
        submitButton.setId("submitButton");
        submitButton.setOnAction(e -> submitGuess());
        feedbackLabel = new Label();
        feedbackLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        feedbackLabel.setTextFill(Color.WHITE);
        HBox timerBox = new HBox(10);
        timerBox.setAlignment(Pos.TOP_LEFT);
        timerBox.getChildren().add(timeLabel);
        layout.getChildren().addAll(titleLabel, instructionsLabel, timerBox, guessGrid, submitButton, feedbackLabel, resultLabel);
        return layout;
    }
    public void startTime() {
        time = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
            }
            updateTimeLabel();
        }));
        time.setCycleCount(Timeline.INDEFINITE);
        time.play();
    }
    private void updateTimeLabel() {
        String time = String.format("%02d:%02d", minutes, seconds);
        timeLabel.setText(time);
    }
    private void generateSecretCode() {
        secretCode = new ArrayList<>(colors);
        Collections.shuffle(secretCode);
        secretCode = secretCode.subList(0, codeLength);
    }
    private void pickColor(Button button, int index) {
        Color chosenColor = chooseColor();
        button.setStyle("-fx-background-color: " + toHexString(chosenColor) + ";");
        guesses[index] = chosenColor;
    }
    private Color chooseColor() {
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }
    private String toHexString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
    private void submitGuess() {
        if (attempts < maxAttempts) {
            boolean allGuessesFilled = true;
            for (Color guess : guesses) {
                if (guess == null) {
                    allGuessesFilled = false;
                    break;
                }
            }
            if (!allGuessesFilled) {
                feedbackLabel.setText("اختار الألوان يا بني");
                feedbackLabel.setTextFill(Color.CORAL);
                return;
            }
            String feedback = giveFeedback(guesses);
            feedbackLabel.setText(feedback);
            attempts++;
            if (isCorrectGuess(guesses)) {
                feedbackLabel.setText("جامد يا قلب اخوك ❤️");
                feedbackLabel.setTextFill(Color.GREEN);
                endGame(true);
            } else if (attempts == maxAttempts) {
                feedbackLabel.setText("جرب تاني بقا");
                feedbackLabel.setTextFill(Color.GRAY);
                endGame(false);
            }
        }
    }
    private String giveFeedback(Color[] guessColors) {
        int correctPosition = 0;
        int correctColor = 0;
        for (int i = 0; i < codeLength; i++) {
            if (guessColors[i] != null && guessColors[i].equals(secretCode.get(i))) {
                correctPosition++;
            } else if (guessColors[i] != null && secretCode.contains(guessColors[i])) {
                correctColor++;
            }
        }
        return "Correct position: " + correctPosition + ", Correct color but wrong position: " + correctColor;
    }
    private void endGame(boolean isWin) {
        time.stop();
        if (isWin) {
            resultLabel.setText("You won in " + String.format("%02d:%02d", minutes, seconds));
            resultLabel.setTextFill(Color.GREEN);
        } else {
            resultLabel.setText("You lost after " + String.format("%02d:%02d", minutes, seconds));
            resultLabel.setTextFill(Color.RED);
        }
    }
    private boolean isCorrectGuess(Color[] guessColors) {
        return Arrays.equals(guessColors, secretCode.toArray());
    }
}
