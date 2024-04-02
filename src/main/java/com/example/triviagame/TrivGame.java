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

import java.util.*;

public class TrivGame extends Application {

    private static final int NUM_QUESTIONS = 4;

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

        List<String> options1 = Arrays.asList("Maseru", "Lobamba", "Manzini", "Mbabane");
        Image image1 = new Image("maseru.jpg");
        questions.add(new Question("What is the capital city of Lesotho?", "Maseru", options1, image1));

        List<String> options2 = Arrays.asList("Sesotho", "Zulu", "English", "Afrikaans");
        Image image2 = new Image("language.webp");
        questions.add(new Question("What is the official language of Lesotho?", "Sesotho", options2, image2));

        List<String> options3 = Arrays.asList("Mountains", "Oceans", "Deserts", "Seas");
        Image image3 = new Image("Mountain.jpg");
        questions.add(new Question("What is a geographical feature found in Lesotho", "Mountains", options3, image3));

        List<String> options4 = Arrays.asList("Kariba Dam", "Chief Joseph Dam", "Katse Dam", "Tarbela Dam");
        Image image4 = new Image("katseDam.jpg");
        questions.add(new Question("What is the dam found in Lesotho", "Katse Dam", options4, image4));
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
                    handleAnswer(selectedOption);
                } else {
                    showAlert("No answer selected", "Please select an answer before submitting.");
                }
            });

            layout.getChildren().addAll(questionLabel, imageView, optionsBox, submitButton);

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

    private void handleAnswer(String selectedOption) {
        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.isCorrect(selectedOption)) {
            score++;
        }
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

        public Question(String text, String correctAnswer, List<String> options, Image image) {
            this.text = text;
            this.correctAnswer = correctAnswer;
            this.options = new ArrayList<>(options);
            Collections.shuffle(this.options);
            this.image = image;
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

        public boolean isCorrect(String selectedOption) {
            return selectedOption.equals(correctAnswer);
        }
    }
}
