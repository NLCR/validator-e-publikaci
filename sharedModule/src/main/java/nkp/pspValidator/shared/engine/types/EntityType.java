package nkp.pspValidator.shared.engine.types;

/**
 * Created by martin on 28.11.16.
 */
public enum EntityType {

    VOLUME("VOLUME"), TITLE("TITLE"), SUPPLEMENT("SUPPL"), PICTURE("PICT"), //spolecne
    ISSUE("ISSUE"), ARTICLE("ART"), //jen periodika
    CHAPTER("CHAP"); //jen monografie

    private final String dmdSecCode;

    EntityType(String dmdSecCode) {
        this.dmdSecCode = dmdSecCode;
    }

    public String getDmdSecCode() {
        return dmdSecCode;
    }
}