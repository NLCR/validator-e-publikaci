package nkp.pspValidator.shared.engine;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Iso8601Test {

    List<String> regexps8601AtLeastMinutes;
    List<String> regexpsOwnFormat;

    @Before
    public void initRegexps() {
        regexps8601AtLeastMinutes = new ArrayList<>();
        /*
        //see https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s07.html
        //year
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})");
        //year, month
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})-(?<month>1[0-2]|0[1-9])$");
        //year, month, day
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})(?<hyphen>-?)(?<month>1[0-2]|0[1-9])\\k<hyphen>(?<day>3[01]|0[1-9]|[12][0-9])$"); //
        //ordinal date: year + day of the year
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})-?(?<day>36[0-6]|3[0-5][0-9]|[12][0-9]{2}|0[1-9][0-9]|00[1-9])$");
        //week: year + week of the year
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})-?W(?<week>5[0-3]|[1-4][0-9]|0[1-9])$");
        //week day: year + week of the year + day of the week
        regexps8601DateMinutes.add("^(?<year>[0-9]{4})-?W(?<week>5[0-3]|[1-4][0-9]|0[1-9])-?(?<day>[1-7])$");
        */
        //basic
        regexps8601AtLeastMinutes.add("^" +
                "(?<year>[0-9]{4})(?<month>1[0-2]|0[1-9])(?<day>3[01]|0[1-9]|[12][0-9])" +
                "T(?<hour>2[0-3]|[01][0-9])(?<minute>[0-5][0-9])" +
                "(?<timezone>Z|[+-](2[0-3]|[01][0-9])(([0-5][0-9]))?)?" +
                "$");

        //extended
        regexps8601AtLeastMinutes.add("^" +
                "(?<year>[0-9]{4})-(?<month>1[0-2]|0[1-9])-(?<day>3[01]|0[1-9]|[12][0-9])" +
                "T(?<hour>2[0-3]|[01][0-9]):(?<minute>[0-5][0-9])" +
                "(?<timezone>Z|[+-](2[0-3]|[01][0-9])(:([0-5][0-9])?)?)?" +
                "$");


        regexpsOwnFormat = new ArrayList<>();
        regexpsOwnFormat.add("^(?<year>[0-9]{4})$"); //RRRR
        regexpsOwnFormat.add("^(?<month>1[0-2]|0[1-9])\\.(?<year>[0-9]{4})$"); //MM.RRRR
        regexpsOwnFormat.add("^(?<month1>1[0-2]|0[1-9])\\.-(?<month2>1[0-2]|0[1-9])\\.(?<year>[0-9]{4})$"); //MM.-MM.RRR
        regexpsOwnFormat.add("^(?<day>3[01]|0[1-9]|[12][0-9])\\.(?<month>1[0-2]|0[1-9])\\.(?<year>[0-9]{4})$"); //DD.MM.RRRR
        regexpsOwnFormat.add("^(?<day1>3[01]|0[1-9]|[12][0-9])\\.-(?<day2>3[01]|0[1-9]|[12][0-9])\\.(?<month>1[0-2]|0[1-9])\\.(?<year>[0-9]{4})$");//DD.-DD.MM.RRRR
    }

    private boolean isValidIso8601AtLeastMinutes(String str) {
        for (String regexp : regexps8601AtLeastMinutes) {
            if (str.matches(regexp)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidOwn(String str) {
        for (String regexp : regexpsOwnFormat) {
            if (str.matches(regexp)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void ownFormat() {
        //see eborn_mon_2.3 supplement's mods:dateIssued
        assertTrue(isValidOwn("17.11.2020"));
        assertTrue(isValidOwn("16.-17.11.2020"));
        assertTrue(isValidOwn("10.-11.2020"));
        assertTrue(isValidOwn("11.2020"));
        assertTrue(isValidOwn("2020"));
    }

    @Test
    public void iso8601MinutesBasicFormat() {
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535Z"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535+01"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535+0100"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535+0130"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535-23"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535-2300"));
        assertTrue(isValidIso8601AtLeastMinutes("20190830T1535-2330"));

        assertFalse(isValidIso8601AtLeastMinutes("20190830T1535+01:00"));
        assertFalse(isValidIso8601AtLeastMinutes("20190830T1535+01:30"));
        assertFalse(isValidIso8601AtLeastMinutes("20190830T1535-23:00"));
        assertFalse(isValidIso8601AtLeastMinutes("20190830T1535-23:30"));
    }

    @Test
    public void iso8601MinutesExtendedFormat() {
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35Z"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35+01"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35+01:00"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35+01:30"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35-23"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35-23:00"));
        assertTrue(isValidIso8601AtLeastMinutes("2019-08-30T15:35-23:30"));

        assertFalse(isValidIso8601AtLeastMinutes("2019-08-30T15:35+0100"));
        assertFalse(isValidIso8601AtLeastMinutes("2019-08-30T15:35+0130"));
        assertFalse(isValidIso8601AtLeastMinutes("2019-08-30T15:35-2300"));
        assertFalse(isValidIso8601AtLeastMinutes("2019-08-30T15:35-2330"));
    }

    @Test
    public void iso8601MinutesMixedFormats() {
        assertFalse(isValidIso8601AtLeastMinutes("201908-30T15:35"));
        assertFalse(isValidIso8601AtLeastMinutes("2019-0830T15:35"));
        assertFalse(isValidIso8601AtLeastMinutes("2019-08-30T1535"));
        assertFalse(isValidIso8601AtLeastMinutes("20190830T15:36"));
    }

    @Test
    public void iso8601Seconds() {
        //TODO: allow/forbid values with seconds, see https://github.com/rzeh4n/validator-e-publikaci/issues/1
        assertFalse(isValidIso8601AtLeastMinutes("20210309T195538"));
    }

    //TODO: testy pro nižší detail, než minuty - takové věci by projít neměly

   /* @Test
    public void iso8601Year() {
        assertTrue(isIso8601DateMinutes("2019"));
        assertFalse(isIso8601DateMinutes("19"));
    }

    @Test
    public void iso8601YearMonth() {
        assertTrue(isIso8601DateMinutes("2009-03"));
        assertFalse(isIso8601DateMinutes("200903"));
    }

    @Test
    public void iso8601YearMonthDay() {
        assertTrue(isIso8601DateMinutes("20090320"));
        assertTrue(isIso8601DateMinutes("2009-03-20"));
        assertFalse(isIso8601DateMinutes("2009-0320"));
        assertFalse(isIso8601DateMinutes("200903-20"));
    }

    @Test
    public void iso8601OrdinalDate() {
        assertTrue(isIso8601DateMinutes("2008-243"));
        assertFalse(isIso8601DateMinutes("2008-367"));
        assertTrue(isIso8601DateMinutes("2008243"));
        assertFalse(isIso8601DateMinutes("2008367"));
    }

    @Test
    public void iso8601Week() {
        assertTrue(isIso8601DateMinutes("2008-W35"));
        assertTrue(isIso8601DateMinutes("2008W35"));
        assertFalse(isIso8601DateMinutes("2008-W00"));
        assertFalse(isIso8601DateMinutes("2008W0"));
        assertFalse(isIso8601DateMinutes("2008-W54"));
        assertFalse(isIso8601DateMinutes("2008W54"));
        assertFalse(isIso8601DateMinutes("2008-W1"));
        assertFalse(isIso8601DateMinutes("2008W1"));
    }

    @Test
    public void iso8601WeekDay() {
        assertTrue(isIso8601DateMinutes("2008-W35-6"));
        assertTrue(isIso8601DateMinutes("2008W356"));
    }*/

}
