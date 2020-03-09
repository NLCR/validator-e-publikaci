package nkp.pspValidator.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import nkp.pspValidator.shared.DmfDetector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martin Řehánek on 13.12.16.
 */
public class PspDirValidationConfigurationDialogController extends DialogController {

    @FXML
    TextField pspDirTextField;

    @FXML
    ChoiceBox forcedEmonVersionChoiceBox;

    @FXML
    CheckBox forcedEmonVersionCheckBox;

    @FXML
    ChoiceBox forcedEperVersionChoiceBox;

    @FXML
    CheckBox forcedEperVersionCheckBox;

    @FXML
    ChoiceBox preferredEmonVersionChoiceBox;

    @FXML
    CheckBox preferredEmonVersionCheckBox;

    @FXML
    ChoiceBox preferredEperVersionChoiceBox;

    @FXML
    CheckBox preferredEperVersionCheckBox;

    @FXML
    Label errorMessageLabel;

    @FXML
    CheckBox createTxtLog;

    @FXML
    CheckBox createXmlLog;

    @FXML
    ToggleButton verbosityLevel3;

    @FXML
    ToggleButton verbosityLevel2;

    @FXML
    ToggleButton verbosityLevel1;

    @FXML
    ToggleButton verbosityLevel0;

    @FXML
    private void initialize() {
        //System.out.println("initialize");
        //spousti se po Parent root = (Parent) loader.load();

        //verbosity toggle group
        ToggleGroup toggleGroup = new ToggleGroup();
        verbosityLevel0.setToggleGroup(toggleGroup);
        verbosityLevel1.setToggleGroup(toggleGroup);
        verbosityLevel2.setToggleGroup(toggleGroup);
        verbosityLevel3.setToggleGroup(toggleGroup);
        //znemožnění toho, aby nebyla vybrána žádná možnost
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            }
        });
    }

    @Override
    public EventHandler<WindowEvent> getOnCloseEventHandler() {
        return new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //nothing, event not consumed, so just closes
            }
        };
    }


    @Override
    public void startNow() {
        //init views from configuration
        ConfigurationManager mgr = getConfigurationManager();
        //forced
        boolean forcedEmonVersionEnabled = mgr.getBooleanOrDefault(ConfigurationManager.PROP_FORCE_EMON_VERSION_ENABLED, false);
        boolean forcedEperVersionEnabled = mgr.getBooleanOrDefault(ConfigurationManager.PROP_FORCE_EPER_VERSION_ENABLED, false);
        forcedEmonVersionCheckBox.setSelected(forcedEmonVersionEnabled);
        forcedEmonVersionChoiceBox.setDisable(!forcedEmonVersionEnabled);
        forcedEperVersionCheckBox.setSelected(forcedEperVersionEnabled);
        forcedEperVersionChoiceBox.setDisable(!forcedEperVersionEnabled);
        //preferred
        boolean preferredEmonVersionEnabled = mgr.getBooleanOrDefault(ConfigurationManager.PROP_PREFER_EMON_VERSION_ENABLED, false);
        boolean preferredEperVersionEnabled = mgr.getBooleanOrDefault(ConfigurationManager.PROP_PREFER_EPER_VERSION_ENABLED, false);
        preferredEmonVersionCheckBox.setSelected(preferredEmonVersionEnabled);
        preferredEmonVersionChoiceBox.setDisable(!preferredEmonVersionEnabled);
        preferredEperVersionCheckBox.setSelected(preferredEperVersionEnabled);
        preferredEperVersionChoiceBox.setDisable(!preferredEperVersionEnabled);
        //logs
        createTxtLog.setSelected(mgr.getBooleanOrDefault(ConfigurationManager.PROP_PSP_VALIDATION_CREATE_TXT_LOG, false));
        createXmlLog.setSelected(mgr.getBooleanOrDefault(ConfigurationManager.PROP_PSP_VALIDATION_CREATE_XML_LOG, false));
        //log verbosity
        Integer textLogVerbosity = mgr.getIntegerOrNull(ConfigurationManager.PROP_TEXT_LOG_VERBOSITY);
        if (textLogVerbosity != null) {
            switch (textLogVerbosity) {
                case 0:
                    verbosityLevel0.setSelected(true);
                    break;
                case 1:
                    verbosityLevel1.setSelected(true);
                    break;
                case 2:
                    verbosityLevel2.setSelected(true);
                    break;
                case 3:
                    verbosityLevel3.setSelected(true);
                    break;
            }
        }

        //init views from fdmf
        initChoiceBoxes();
    }

    private void initChoiceBoxes() {
        ConfigurationManager mgr = getConfigurationManager();
        //forced - e-Monograph
        List<String> forcedMonVersions = new ArrayList<>();
        forcedMonVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEmonographFdmfVersions());
        Collections.sort(forcedMonVersions, new VersionComparator());
        if (forcedMonVersions != null) {
            ObservableList<String> monVersionsObservable = FXCollections.observableArrayList(forcedMonVersions);
            forcedEmonVersionChoiceBox.setItems(monVersionsObservable);
            String version = mgr.getStringOrDefault(ConfigurationManager.PROP_FORCE_EMON_VERSION_CODE, null);
            boolean found = false;
            if (version != null) {
                for (int i = 0; i < monVersionsObservable.size(); i++) {
                    if (version.equals(monVersionsObservable.get(i))) {
                        forcedEmonVersionChoiceBox.getSelectionModel().select(i);
                        found = true;
                    }
                }
            }
            if (!found) {
                forcedEmonVersionChoiceBox.getSelectionModel().selectFirst();
            }
        }
        //forced - e-Periodical
        List<String> forcedPerVersions = new ArrayList<>();
        forcedPerVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEperiodicalFdmfVersions());
        Collections.sort(forcedPerVersions, new VersionComparator());
        if (forcedPerVersions != null) {
            ObservableList<String> perVersionsObservable = FXCollections.observableArrayList(forcedPerVersions);
            forcedEperVersionChoiceBox.setItems(perVersionsObservable);
            String version = mgr.getStringOrDefault(ConfigurationManager.PROP_FORCE_EPER_VERSION_CODE, null);
            boolean found = false;
            if (version != null) {
                for (int i = 0; i < perVersionsObservable.size(); i++) {
                    if (version.equals(perVersionsObservable.get(i))) {
                        forcedEperVersionChoiceBox.getSelectionModel().select(i);
                        found = true;
                    }
                }
            }
            if (!found) {
                forcedEperVersionChoiceBox.getSelectionModel().selectFirst();
            }
        }
        //preferred - e-Monograph
        List<String> preferredEmonVersions = new ArrayList<>();
        preferredEmonVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEmonographFdmfVersions());
        Collections.sort(preferredEmonVersions, new VersionComparator());
        if (preferredEmonVersions != null) {
            ObservableList<String> monVersionsObservable = FXCollections.observableArrayList(preferredEmonVersions);
            preferredEmonVersionChoiceBox.setItems(monVersionsObservable);
            String version = mgr.getStringOrDefault(ConfigurationManager.PROP_PREFER_EMON_VERSION_CODE, null);
            boolean found = false;
            if (version != null) {
                for (int i = 0; i < monVersionsObservable.size(); i++) {
                    if (version.equals(monVersionsObservable.get(i))) {
                        preferredEmonVersionChoiceBox.getSelectionModel().select(i);
                        found = true;
                    }
                }
            }
            if (!found) {
                preferredEmonVersionChoiceBox.getSelectionModel().selectFirst();
            }
        }
        //preferred - e-Periodical
        List<String> preferredPerVersions = new ArrayList<>();
        preferredPerVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEperiodicalFdmfVersions());
        Collections.sort(preferredPerVersions, new VersionComparator());
        if (preferredPerVersions != null) {
            ObservableList<String> perVersionsObservable = FXCollections.observableArrayList(preferredPerVersions);
            preferredEperVersionChoiceBox.setItems(perVersionsObservable);
            String version = mgr.getStringOrDefault(ConfigurationManager.PROP_PREFER_EPER_VERSION_CODE, null);
            boolean found = false;
            if (version != null) {
                for (int i = 0; i < perVersionsObservable.size(); i++) {
                    if (version.equals(perVersionsObservable.get(i))) {
                        preferredEperVersionChoiceBox.getSelectionModel().select(i);
                        found = true;
                    }
                }
            }
            if (!found) {
                preferredEperVersionChoiceBox.getSelectionModel().selectFirst();
            }
        }
    }

    public void selectPspDir(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Vyberte kořenový adresář PSP balíku");
        File lastPspDir = getConfigurationManager().getFileOrNull(ConfigurationManager.PROP_LAST_PSP_DIR);
        if (lastPspDir != null && lastPspDir.exists()) {
            File parent = lastPspDir.getParentFile();
            if (parent != null && parent.exists()) {
                chooser.setInitialDirectory(parent);
            }
        }
        File selectedDir = chooser.showDialog(stage);
        if (selectedDir != null) {
            getConfigurationManager().setFile(ConfigurationManager.PROP_LAST_PSP_DIR, selectedDir);
            try {
                pspDirTextField.setText(selectedDir.getCanonicalPath());
            } catch (IOException e) {
                //should never happen
                throw new RuntimeException(e);
            }
        }
    }

    public void closeDialog(ActionEvent actionEvent) {
        stage.close();
    }


    public void validate(ActionEvent actionEvent) {
        String pspDirTxt = pspDirTextField.getText();
        if (pspDirTxt == null || pspDirTxt.isEmpty()) {
            showError("Prázdné jméno adresáře PSP balíku!");
        } else {
            File pspDir = new File(pspDirTxt.trim());
            if (!pspDir.exists()) {
                showError(String.format("Adresář '%s' neexistuje!", pspDirTxt));
            } else if (!pspDir.isDirectory()) {
                showError(String.format("Soubor '%s' není adresář!", pspDirTxt));
            } else if (!pspDir.canRead()) {
                showError(String.format("Nelze číst obsah adresáře '%s'!", pspDirTxt));
            } else {
                DmfDetector.Params params = new DmfDetector.Params();
                params.forcedDmfEmonVersion = forcedEmonVersionChoiceBox.isDisabled() ? null : (String) forcedEmonVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.forcedDmfEperVersion = forcedEperVersionChoiceBox.isDisabled() ? null : (String) forcedEperVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.preferredDmfEmonVersion = preferredEmonVersionChoiceBox.isDisabled() ? null : (String) preferredEmonVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.preferredDmfEperVersion = preferredEperVersionChoiceBox.isDisabled() ? null : (String) preferredEperVersionChoiceBox.getSelectionModel().getSelectedItem();
                int verbosity = getSelectedVerbosity();
                stage.hide();
                main.runPspDirValidation(pspDir, params, createTxtLog.isSelected(), createXmlLog.isSelected(), verbosity);
            }
        }
    }

    private int getSelectedVerbosity() {
        if (verbosityLevel0.isSelected()) {
            return 0;
        } else if (verbosityLevel1.isSelected()) {
            return 1;
        } else if (verbosityLevel2.isSelected()) {
            return 2;
        } else if (verbosityLevel3.isSelected()) {
            return 3;
        } else {
            throw new IllegalStateException("no ToggleButton selected");
        }
    }

    private void showError(String s) {
        errorMessageLabel.setText(s);
    }

    public void forcedEmonVersionChanged(ActionEvent actionEvent) {
        boolean forced = forcedEmonVersionCheckBox.isSelected();
        forcedEmonVersionChoiceBox.setDisable(!forced);
        if (getConfigurationManager() != null) {
            getConfigurationManager().setBoolean(ConfigurationManager.PROP_FORCE_EMON_VERSION_ENABLED, forced);
        }
    }

    public void forcedEperVersionChanged(ActionEvent actionEvent) {
        boolean forced = forcedEperVersionCheckBox.isSelected();
        forcedEperVersionChoiceBox.setDisable(!forced);
        if (getConfigurationManager() != null) {
            getConfigurationManager().setBoolean(ConfigurationManager.PROP_FORCE_EPER_VERSION_ENABLED, forced);
        }
    }

    public void forcedEmonVersionChoiceboxChanged(ActionEvent actionEvent) {
        String version = (String) forcedEmonVersionChoiceBox.getSelectionModel().getSelectedItem();
        if (getConfigurationManager() != null) {
            getConfigurationManager().setString(ConfigurationManager.PROP_FORCE_EMON_VERSION_CODE, version);
        }
    }

    public void forcedEperVersionChoiceboxChanged(ActionEvent actionEvent) {
        String version = (String) forcedEperVersionChoiceBox.getSelectionModel().getSelectedItem();
        if (getConfigurationManager() != null) {
            getConfigurationManager().setString(ConfigurationManager.PROP_FORCE_EPER_VERSION_CODE, version);
        }
    }

    public void preferredEmonVersionChanged(ActionEvent actionEvent) {
        boolean preferred = preferredEmonVersionCheckBox.isSelected();
        preferredEmonVersionChoiceBox.setDisable(!preferred);
        if (getConfigurationManager() != null) {
            getConfigurationManager().setBoolean(ConfigurationManager.PROP_PREFER_EMON_VERSION_ENABLED, preferred);
        }
    }

    public void preferredEperVersionChanged(ActionEvent actionEvent) {
        boolean preferred = preferredEperVersionCheckBox.isSelected();
        preferredEperVersionChoiceBox.setDisable(!preferred);
        if (getConfigurationManager() != null) {
            getConfigurationManager().setBoolean(ConfigurationManager.PROP_PREFER_EPER_VERSION_ENABLED, preferred);
        }
    }

    public void preferredEmonVersionChoiceboxChanged(ActionEvent actionEvent) {
        String version = (String) preferredEmonVersionChoiceBox.getSelectionModel().getSelectedItem();
        if (getConfigurationManager() != null) {
            getConfigurationManager().setString(ConfigurationManager.PROP_PREFER_EMON_VERSION_CODE, version);
        }
    }

    public void preferredEperVersionChoiceboxChanged(ActionEvent actionEvent) {
        String version = (String) preferredEperVersionChoiceBox.getSelectionModel().getSelectedItem();
        if (getConfigurationManager() != null) {
            getConfigurationManager().setString(ConfigurationManager.PROP_PREFER_EPER_VERSION_CODE, version);
        }
    }

    public void createXmlLogChanged(ActionEvent actionEvent) {
        boolean create = createXmlLog.isSelected();
        getConfigurationManager().setBoolean(ConfigurationManager.PROP_PSP_VALIDATION_CREATE_XML_LOG, create);
    }

    public void createTxtLogChanged(ActionEvent actionEvent) {
        boolean create = createTxtLog.isSelected();
        getConfigurationManager().setBoolean(ConfigurationManager.PROP_PSP_VALIDATION_CREATE_TXT_LOG, create);
    }

    public void onVerbositySwitched(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        Integer verbosity = null;
        if (source == verbosityLevel0) {
            verbosity = 0;
        } else if (source == verbosityLevel1) {
            verbosity = 1;
        } else if (source == verbosityLevel2) {
            verbosity = 2;
        } else if (source == verbosityLevel3) {
            verbosity = 3;
        }
        getConfigurationManager().setInteger(ConfigurationManager.PROP_TEXT_LOG_VERBOSITY, verbosity);
    }

}