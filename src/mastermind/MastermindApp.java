package mastermind;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class MastermindApp extends Application {
    private static final int codeLength = 4;
    private static final int maxAttempts = 15;
    private List<Color> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.GRAY);
    private List<Color> secretCode;
    private int attempts;
    private Color[] guesses = new Color[codeLength];
    private VBox layout;
    private GridPane guessGrid;
    private Label feedbackLabel;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mastermind Game");
        attempts = 0;
        generateSecretCode();
        layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Welcome to Mastermind!");
        titleLabel.setFont(new Font("Arial", 24));
        Label instructionsLabel = new Label("Guess the secret code by select 4 colors.\nYou have 15 attempts.");
        instructionsLabel.setFont(new Font("Arial", 14));
        guessGrid = new GridPane();
        guessGrid.setHgap(10);
        guessGrid.setVgap(10);
        guessGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < codeLength; i++) {
            Button colorButton = new Button("Pick Color");
            colorButton.setStyle("-fx-background-color: gray;");
            colorButton.setPrefSize(80, 80);
            final int index = i;
            colorButton.setOnAction(e -> pickColor(colorButton, index));
            guessGrid.add(colorButton, i, 0);
        }
        Button submitButton = new Button("Submit Guess");
        submitButton.setOnAction(e -> submitGuess());
        feedbackLabel = new Label();
        feedbackLabel.setFont(new Font("Arial", 14));
        layout.getChildren().addAll(titleLabel, instructionsLabel, guessGrid, submitButton, feedbackLabel);
        Scene scene = new Scene(layout, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("mastermind.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void generateSecretCode() {
        secretCode = new ArrayList<>(colors);
        Collections.shuffle(secretCode);
        secretCode = secretCode.subList(0, codeLength);
    }
    private void pickColor(Button button, int index) {
        Color chosenColor = chooseRandomColor();
        button.setStyle("-fx-background-color: " + toHexString(chosenColor) + ";");
        guesses[index] = chosenColor;
    }
    private String toHexString(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }
    private Color chooseRandomColor() {
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }
    private String giveFeedback(Color[] guessColors) {
        int correctPosition = 0;
        int correctColor = 0;
        for (int i = 0; i < codeLength; i++) {
            if (guessColors[i].equals(secretCode.get(i))) {
                correctPosition++;
            } else if (secretCode.contains(guessColors[i])) {
                correctColor++;
            }
        }
        return "Correct position: " + correctPosition + ", Correct color but wrong position: " + correctColor;
    }
    private void submitGuess() {
        if (attempts < maxAttempts) {
            String feedback = giveFeedback(guesses);
            feedbackLabel.setText(feedback);
            attempts++;
            if (isCorrectGuess(guesses)) {
                feedbackLabel.setText("Congratulations! You guessed correctly!");
                feedbackLabel.setTextFill(Color.GREEN);
            } else if (attempts == maxAttempts) {
                feedbackLabel.setText("Game Over! The secret code was: " + secretCode);
                feedbackLabel.setTextFill(Color.AQUA);
            }
        }
    }
    private boolean isCorrectGuess(Color[] guessColors) {
        return Arrays.equals(guessColors, secretCode.toArray());
    }
}