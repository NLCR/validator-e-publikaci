package nkp.pspValidator.shared.engine;

/**
 * Created by martin on 21.10.16.
 */
public enum ValueType {

    STRING, INTEGER, FILE, IDENTIFIER,//
    STRING_LIST, FILE_LIST, IDENTIFIER_LIST, //lists
    STRING_LIST_LIST, FILE_LIST_LIST, IDENTIFIER_LIST_LIST,  //lists of lists
    LEVEL, IMAGE_COPY, IMAGE_UTIL, METADATA_FORMAT, ENTITY_TYPE; //enums

}
