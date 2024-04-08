package com.example.triviagame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.*;

public class TrivGame extends Application {

    private static final int NUM_QUESTIONS = 5;

    private Stage stage;
    private List<Scene> questionScenes;
    private int currentQuestionIndex;
    private int score;

    private List<Question> questions;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        initializeQuestions();

        questionScenes = createQuestionScenes();

        showNextQuestion();
    }

    private void initializeQuestions() {
        questions = new ArrayList<>();

        // Add questions with associated images and video clips
        Image maseruImage = new Image(getClass().getResource("/com/example/triviagame/maseru.jpg").toExternalForm());
        questions.add(new Question("What is the capital city of Lesotho?", "Maseru",
                Arrays.asList("Maseru", "Lobamba", "Manzini", "Mbabane"),
                maseruImage, "/com/example/triviagame/maseru.mp4"));

        Image katseDamImage = new Image(getClass().getResource("/com/example/triviagame/katseDam.jpg").toExternalForm());
        questions.add(new Question("What is the dam found in Lesotho?", "Katse Dam",
                Arrays.asList("Kariba Dam", "Chief Joseph Dam", "Katse Dam", "Tarbela Dam"),
                katseDamImage, "/com/example/triviagame/katse.mp4"));

        // Add more questions similarly
    }





    private List<Scene> createQuestionScenes() {
        List<Scene> scenes = new ArrayList<>();

        for (Question question : questions) {
            VBox layout = new VBox(20);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(20));

            Label questionLabel = new Label(question.getText());
            questionLabel.setStyle("-fx-font-size: 18px;");

            ImageView imageView = new ImageView(question.getImage());
            imageView.setFitWidth(400);
            imageView.setFitHeight(300);

            MediaPlayer mediaPlayer = new MediaPlayer(question.getVideoClip());
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
            Button playVideoButton = new Button("Play Video");
            playVideoButton.setOnAction(e -> mediaPlayer.play());

            VBox optionsBox = new VBox(10);
            ToggleGroup group = new ToggleGroup();
            question.getOptions().forEach(option -> {
                RadioButton optionButton = new RadioButton(option);
                optionButton.setToggleGroup(group);
                optionsBox.getChildren().add(optionButton);
            });

            Button submitButton = new Button("Submit");
            submitButton.setOnAction(e -> {
                RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                if (selectedRadioButton != null) {
                    String selectedOption = selectedRadioButton.getText();
                    handleAnswer(selectedOption, mediaPlayer);
                } else {
                    showAlert("No answer selected", "Please select an answer before submitting.");
                }
            });

            layout.getChildren().addAll(questionLabel, imageView, playVideoButton, optionsBox, submitButton);

            Scene scene = new Scene(layout, 800, 600);
            scenes.add(scene);
        }

        return scenes;
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < NUM_QUESTIONS) {
            stage.setScene(questionScenes.get(currentQuestionIndex));
        } else {
            showResultScene();
        }
    }

    private void handleAnswer(String selectedOption, MediaPlayer mediaPlayer) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.isCorrect(selectedOption)) {
            score++;
        }
        mediaPlayer.stop();
        currentQuestionIndex++;
        showNextQuestion();
    }

    private void showResultScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #81D4FA;");

        Label endLabel = new Label("Quiz completed!");
        endLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label scoreLabel = new Label("Your score: " + score + " / " + NUM_QUESTIONS);
        scoreLabel.setStyle("-fx-font-size: 18px;");

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> {
            score = 0;
            currentQuestionIndex = 0;
            showNextQuestion();
        });

        layout.getChildren().addAll(endLabel, scoreLabel, restartButton);

        Scene resultScene = new Scene(layout, 800, 600);
        stage.setScene(resultScene);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static class Question {
        private String text;
        private String correctAnswer;
        private List<String> options;
        private Image image;
        private Media videoClip;

        public Question(String text, String correctAnswer, List<String> options, Image image, String videoPath) {
            this.text = text;
            this.correctAnswer = correctAnswer;
            this.options = new ArrayList<>(options);
            Collections.shuffle(this.options);
            this.image = image;
            this.videoClip = new Media(getClass().getResource(videoPath).toExternalForm());
        }

        public String getText() {
            return text;
        }

        public List<String> getOptions() {
            return options;
        }

        public Image getImage() {
            return image;
        }

        public Media getVideoClip() {
            return videoClip;
        }

        public boolean isCorrect(String selectedOption) {
            return selectedOption.equals(correctAnswer);
        }
    }
}
