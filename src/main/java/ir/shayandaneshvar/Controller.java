package ir.shayandaneshvar;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private List<Pair<Double, Double>> coordinates;
    @FXML
    private JFXTextField yField;
    @FXML
    private JFXTextField xField;
    @FXML
    private AnchorPane drawPane;

    @FXML
    void addCoordinate() {
        var x = Double.parseDouble(xField.getText());
        var y = Double.parseDouble(yField.getText());
        var pair = new Pair<>(x, y);
        coordinates.add(pair);
        xField.setText("");
        yField.setText("");
        drawPolygon(coordinates);
    }

    @FXML
    void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            xField.requestFocus();
            addCoordinate();
        }
    }

    @FXML
    void reset() {
        coordinates.clear();
        drawPane.getChildren().clear();
    }

    @FXML
    void triangulate() {
        List<Pair<Pair<Double, Double>, Pair<Double, Double>>> result =
                new ArrayList<>();
        double cost = getLines(result);
        drawLines(result);
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "The Minimum Cost is: " + cost, ButtonType.OK);
        alert.setTitle("Done!");
        alert.setHeaderText("Triangulated Successfully!");
        alert.show();
    }

    private double distance(Pair<Double, Double> p1,
                            Pair<Double, Double> p2) {
        return Math.sqrt(Math.pow(p1.getKey() - p2.getKey(), 2) +
                Math.pow(p1.getValue() - p2.getValue(), 2));
    }

    private void drawLines(List<Pair<Pair<Double, Double>, Pair<Double, Double>>> result) {
        result.forEach(z -> {
            var from = z.getKey();
            var to = z.getValue();
            Line line = new Line(from.getKey() * 10, 420 - from.getValue() * 10,
                    to.getKey() * 10, 420 - to.getValue() * 10);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStroke(Color.YELLOWGREEN);
            line.setStrokeWidth(3);
            drawPane.getChildren().add(line);
        });
    }

    private void drawPolygon(
            List<Pair<Double, Double>> coords) {
        drawPane.getChildren().clear();
        Pair<Double, Double> current, prev = coords.get(0);
        for (int i = 1; i < coords.size(); i++) {
            current = coords.get(i);
            var line = new Line(prev.getKey() * 10,
                    420 - prev.getValue() * 10,
                    current.getKey() * 10, 420 - current.getValue() * 10);
            prev = current;
            line.setStrokeWidth(4);
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            line.setStroke(Color.CYAN);
            drawPane.getChildren().add(line);
        }
        Line line = new Line(coords.get(0).getKey() * 10,
                420 - coords.get(0).getValue() * 10,
                prev.getKey() * 10, 420 - prev.getValue() * 10);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(4);
        line.setStroke(Color.CYAN);
        drawPane.getChildren().add(line);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coordinates = new ArrayList<>();
    }

    double getLines(List<Pair<Pair<Double, Double>, Pair<Double, Double>>>
                            result) {
        double[][] lengths = new double[coordinates.size()][coordinates.size()];
        int[][] answer = new int[coordinates.size()][coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = 0; j < coordinates.size(); j++) {
                answer[i][j] = 0;
            }
        }
        for (int diagonal = 0; diagonal < coordinates.size(); diagonal++) {
            for (int i = 0, j = diagonal; j < coordinates.size(); i++, j++) {
                if (j - 2 < i) {
                    lengths[i][j] = 0.0;
                } else {
                    lengths[i][j] = Double.MAX_VALUE;
                    for (int k = i + 1; k < j; k++) {
                        double val = lengths[i][k] + lengths[k][j] +
                                getCost(coordinates, i, j, k);
                        if (lengths[i][j] > val) {
                            lengths[i][j] = val;
                            answer[i][j] = k;
                        }
                    }
                }
            }
        }
        getAnswer(result, coordinates, answer, 0, coordinates.size() - 1);
        return lengths[0][coordinates.size() - 1] / 2;
    }

    private void getAnswer(List<Pair<Pair<Double, Double>, Pair<Double, Double>
            >> out, List<Pair<Double, Double>> coordinates,
                           int[][] answer, int i, int j) {
        if (i < 0 || j < 0 || Math.abs(i - j) < 2) {
            return;
        }
        int k = answer[i][j];
        if (Math.abs(i - j) > 1 && Math.abs(i - j) < coordinates.size() - 1) {
            out.add(new Pair<>(coordinates.get(i), coordinates.get(j)));
        }if (Math.abs(j - k) > 1 && Math.abs(j - k) < coordinates.size() - 1) {
            out.add(new Pair<>(coordinates.get(k), coordinates.get(j)));
        }if (Math.abs(i - k) > 1 && Math.abs(i - k) < coordinates.size() - 1) {
            out.add(new Pair<>(coordinates.get(k), coordinates.get(i)));
        }
        getAnswer(out, coordinates, answer, i, k);
        getAnswer(out, coordinates, answer, k, j);
    }
    private double getCost(List<Pair<Double, Double>> pairs, int i, int j, int k) {
        var p1 = pairs.get(i);
        var p2 = pairs.get(j);
        var pk = pairs.get(k);
        var c12 = Math.abs(i - j) > 1 && Math.abs(i - j) < coordinates.size()
                - 1 ? distance(p1, p2) : 0;
        var c2k = Math.abs(j - k) > 1 && Math.abs(j - k) < coordinates.size()
                - 1 ? distance(p2, pk) : 0;
        var pk1 = Math.abs(i - k) > 1 && Math.abs(i - k) < coordinates.size()
                - 1 ? distance(pk, p1) : 0;
        return c12 + c2k + pk1;
    }

}
