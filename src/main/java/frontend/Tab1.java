package frontend;

import backend.JsonParser.JsonParser;
import backend.currencies.Currency;
import backend.interfaces.implementations.DataManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tab1 {
	@FXML
	public Label test;
	public ComboBox currencyList;
	public ComboBox period;
	public Label mediana;
	public Label dominanta;
	public Label odchylenie;
	public Label zmiennosci;
	public Button showDataButton;
	public LineChart<String, Double> currencyChart;

	DataManager dm = new DataManager();
	List<Currency> cl;

	public void loadData() throws IOException {
		JsonParser j = new JsonParser();
		cl = j.getAvailableCurrenciesList();
		setCurrencyRates(cl);
		setPeriods();
	}

	private void setPeriods() {
		ArrayList<String> p = new ArrayList<>();
		p.add("Tydzień");
		p.add("2 tygodnie");
		p.add("Miesiąc");
		p.add("Kwartał");
		p.add("6 miesięcy");
		p.add("Rok");
		period.setItems(FXCollections.observableList(p));
	}

	private void setCurrencyRates(List<Currency> cl) {
		ArrayList<String> names = new ArrayList<>();
		for(Currency c : cl)
			names.add(c.getName());
		currencyList.setItems(FXCollections.observableList(names));
	}

	public void showData(MouseEvent mouseEvent) throws IOException {
		String currentName = (String) currencyList.getSelectionModel().getSelectedItem();
		Currency current = null;
		for(Currency c : cl) {
			if (c.getName().equals(currentName)) {
				current = c;
				break;
			}
		}
		List<Currency> c = dm.getForPeriod(current, "2022-06-01", "2022-07-02");
		setMediana(c);
		setDominanta(c);
		setOdchylenie(c);
		setZmiennosci(c);
		fillTheChart(c);
	}

	private void fillTheChart(List<Currency> c) {
		currencyChart.getData().clear();
		XYChart.Series<String, Double> series = new XYChart.Series<String, Double>();
		for (Currency curr : c)
			series.getData().add(new XYChart.Data<String, Double>(curr.getDate(),Double.parseDouble(curr.getValue())));
		series.setName(c.get(0).getName());
		currencyChart.getData().addAll(series);
		currencyChart.getXAxis().setAnimated(false);
		currencyChart.getXAxis().lookup(".axis-label").setStyle("-fx-label-padding: -10 0 0 0;");
	}

	private void setZmiennosci(List<Currency> c) {
		Double sum = 0.0;
		for (Currency curr : c)
			sum+=Double.parseDouble(curr.getValue());
		zmiennosci.setText(String.valueOf(Double.parseDouble(odchylenie.getText())/(sum/c.size())));
	}

	private void setOdchylenie(List<Currency> c) {
		ArrayList<Double> sDList = new ArrayList<>();
		for (Currency curr : c)
			sDList.add(Double.valueOf(curr.getValue()));
		double sum = 0.0, standard_deviation = 0.0;
		int array_length = sDList.size();
		for(double temp : sDList) {
			sum += temp;
		}
		double mean = sum/array_length;
		for(double temp: sDList) {
			standard_deviation += Math.pow(temp - mean, 2);
		}
		odchylenie.setText(String.valueOf(Math.sqrt(standard_deviation/array_length)));
	}

	private void setDominanta(List<Currency> c) {
		ArrayList<Double> modeList = new ArrayList<>();
		for (Currency curr : c)
			modeList.add(Double.valueOf(curr.getValue()));
		int maxCount = 0;
		Double maxValue = 0.0;
		for (int i=0;i<modeList.size();i++){
			int count = 0;
			for (int j = 0; j < modeList.size(); ++j) {
				if (modeList.get(j).equals(modeList.get(i)))
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				maxValue = modeList.get(i);
			}
		}
		dominanta.setText(String.valueOf(maxValue));

	}

	private void setMediana(List<Currency> c) {
		ArrayList<Double> medianList = new ArrayList<>();
		for (Currency curr : c)
			medianList.add(Double.valueOf(curr.getValue()));
		Collections.sort(medianList);
		Double middle = (medianList.get(medianList.size()/2) + medianList.get(medianList.size()/2 - 1))/2;
		mediana.setText(String.valueOf(middle));
	}
}
