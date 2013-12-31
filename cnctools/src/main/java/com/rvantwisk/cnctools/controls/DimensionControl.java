package com.rvantwisk.cnctools.controls;

import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.Dimensions;
import com.rvantwisk.cnctools.misc.InputMaskChecker;
import com.rvantwisk.cnctools.misc.RestrictiveTextField;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 11/29/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DimensionControl extends HBox {
    private final DecimalFormat SHORTESTFORMATTER = new DecimalFormat("#.#########"); // Formatting and trimming of numbers

    @FXML
    private RestrictiveTextField iValue;

    @FXML
    private ComboBox iDimension;
    private Dimensions.Type dimensionType = Dimensions.Type.LENGTH;

    private final DimensionProperty dimension = new DimensionProperty();

    public DimensionControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dimension.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        assert iValue != null : "fx:id=\"iValue\" was not injected: check your FXML file 'ToolParameters.fxml'.";
        assert iDimension != null : "fx:id=\"iDimension\" was not injected: check your FXML file 'ToolParameters.fxml'.";

        final InputMaskChecker listener1 = new InputMaskChecker(InputMaskChecker.NOTEMPTY, iValue);

        iValue.textProperty().addListener(listener1);

        final BooleanBinding binding = new BooleanBinding() {
            {
                super.bind(listener1.erroneous);
            }

            @Override
            protected boolean computeValue() {
                return (listener1.erroneous.get());
            }
        };
        binding.invalidate();

        iValue.setRestrict("[0-9]*(.[0-9]*)?([eE][-+][0-9]*)?");
        iValue.textProperty().bindBidirectional(dimension.valueProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return SHORTESTFORMATTER.format(t.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        });

        // When dimension property is updated, select the correct combobox item
        dimension.dimensionProperty().addListener(new ChangeListener<Dimensions.Dim>() {
            @Override
            public void changed(ObservableValue<? extends Dimensions.Dim> observableValue, Dimensions.Dim dim, Dimensions.Dim dim2) {
                final int i = Dimensions.getIndex(dimensionType, dimension.getDimension());
                iDimension.getSelectionModel().select(i);
            }
        });

        // Set the dimention value when the dimention dropdown was changed
        iDimension.valueProperty().addListener(new ChangeListener<Dimensions.Item>() {
            @Override
            public void changed(ObservableValue<? extends Dimensions.Item> observableValue, Dimensions.Item item, Dimensions.Item item2) {
                dimension.setDimension(item2.getKey());
            }
        });

        // DIsplay the correct value in teh combobox
        Callback<ListView<Dimensions.Item>,ListCell<Dimensions.Item>> cellFactory = new Callback<ListView<Dimensions.Item>, ListCell<Dimensions.Item>>() {
            @Override public ListCell<Dimensions.Item> call(ListView<Dimensions.Item> p) {
                return new ListCell<Dimensions.Item>() {

                    @Override protected void updateItem(Dimensions.Item item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getValue());
                        }
                    }
                };
            }};

        iDimension.setButtonCell(cellFactory.call(null));
        iDimension.setCellFactory(cellFactory);
    }

    public void showDimention() {
        iDimension.getItems().clear();
        iDimension.getItems().addAll(Dimensions.getList(this.dimensionType));
    }

    public DimensionProperty dimensionProperty() {
        return dimension;
    }

    public Dimensions.Type getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(Dimensions.Type dimensionType) {
        this.dimensionType = dimensionType;
        showDimention();
    }

}