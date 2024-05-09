package nkp.pspValidator.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
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
public class PspZipValidationConfigurationDialogController extends DialogController {

    @FXML
    TextField pspZipTextField;

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
        //forced - E-Monograph
        List<String> forcedEmonVersions = new ArrayList<>();
        forcedEmonVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEmonographFdmfVersions());
        Collections.sort(forcedEmonVersions, new VersionComparator());
        if (forcedEmonVersions != null) {
            ObservableList<String> monVersionsObservable = FXCollections.observableArrayList(forcedEmonVersions);
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
        //forced - E-Periodical
        List<String> forcedEperVersions = new ArrayList<>();
        forcedEperVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEperiodicalFdmfVersions());
        Collections.sort(forcedEperVersions, new VersionComparator());
        if (forcedEperVersions != null) {
            ObservableList<String> perVersionsObservable = FXCollections.observableArrayList(forcedEperVersions);
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
        //preferred - E-Monograph
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
        //preferred - E-Periodical
        List<String> preferredEperVersions = new ArrayList<>();
        preferredEperVersions.addAll(main.getValidationDataManager().getFdmfRegistry().getEperiodicalFdmfVersions());
        Collections.sort(preferredEperVersions, new VersionComparator());
        if (preferredEperVersions != null) {
            ObservableList<String> perVersionsObservable = FXCollections.observableArrayList(preferredEperVersions);
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
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Vyberte soubor PSP balíku");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Soubory ZIP", "*.zip"));
        File lastPspZip = getConfigurationManager().getFileOrNull(ConfigurationManager.PROP_LAST_PSP_ZIP);
        if (lastPspZip != null && lastPspZip.exists()) {
            File parent = lastPspZip.getParentFile();
            if (parent != null && parent.exists()) {
                chooser.setInitialDirectory(parent);
            }
        }
        File selectedZipFile = chooser.showOpenDialog(stage);
        if (selectedZipFile != null) {
            getConfigurationManager().setFile(ConfigurationManager.PROP_LAST_PSP_ZIP, selectedZipFile);
            try {
                pspZipTextField.setText(selectedZipFile.getCanonicalPath());
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
        String pspZipFileTxt = pspZipTextField.getText();
        if (pspZipFileTxt == null || pspZipFileTxt.isEmpty()) {
            showError("Prázdné jméno zip souboru PSP balíku!");
        } else {
            File pspZipFile = new File(pspZipFileTxt.trim());
            if (!pspZipFile.exists()) {
                showError(String.format("Soubor '%s' neexistuje!", pspZipFileTxt));
            } else if (!pspZipFile.isFile()) {
                showError(String.format("Soubor '%s' není normální soubor !", pspZipFileTxt));
            } else if (!pspZipFile.canRead()) {
                showError(String.format("Nelze číst soubor '%s'!", pspZipFileTxt));
            } else {
                DmfDetector.Params params = new DmfDetector.Params();
                //TODO:
                /*params.forcedDmfMonVersion = forcedMonVersionChoiceBox.isDisabled() ? null : (String) forcedMonVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.forcedDmfPerVersion = forcedPerVersionChoiceBox.isDisabled() ? null : (String) forcedPerVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.forcedDmfSRVersion = forcedSRVersionChoiceBox.isDisabled() ? null : (String) forcedSRVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.preferredDmfMonVersion = preferredMonVersionChoiceBox.isDisabled() ? null : (String) preferredMonVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.preferredDmfPerVersion = preferredPerVersionChoiceBox.isDisabled() ? null : (String) preferredPerVersionChoiceBox.getSelectionModel().getSelectedItem();
                params.preferredDmfSRVersion = preferredSRVersionChoiceBox.isDisabled() ? null : (String) preferredSRVersionChoiceBox.getSelectionModel().getSelectedItem();*/
                int verbosity = getSelectedVerbosity();
                //stage.hide();
                main.unzipAndRunPspZipValidation(pspZipFile, params, createTxtLog.isSelected(), createXmlLog.isSelected(), verbosity);
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
