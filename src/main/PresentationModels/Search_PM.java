package main.PresentationModels;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import main.Utils;

public class Search_PM {
    private BooleanProperty searchBool = new SimpleBooleanProperty();
    private StringProperty searchText = new SimpleStringProperty();

    private BooleanBinding searchBoolInverted = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return !searchBool.get();
        }
    };
    private BooleanBinding searchBoolToVisibility = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return searchBool.get();
        }
    };
    private BooleanBinding notEmptyToVisibility = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return Utils.isNullOrEmpty(searchText.get());
        }
    };

    public final BooleanProperty searchBoolProperty() {
        return searchBool;
    }
    public final BooleanBinding searchBoolInvertedBinding() {
        return searchBoolInverted;
    }
    public BooleanBinding searchBoolToVisibilityBinding() {
        return searchBoolToVisibility;
    }
    public final StringProperty searchTextProperty() {
        return searchText;
    }
    public BooleanBinding notEmptyToVisibilityBinding() {
        return notEmptyToVisibility;
    }

    public Search_PM() {
        searchText.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                notEmptyToVisibility.invalidate();
            }
        });
    }
}
