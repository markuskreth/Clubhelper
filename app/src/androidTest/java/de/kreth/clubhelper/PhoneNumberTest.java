package de.kreth.clubhelper;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import junit.framework.TestCase;

/**
 * Created by markus on 26.02.15.
 */
public class PhoneNumberTest extends TestCase {
    private PhoneNumberUtil phoneUtil;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        phoneUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        phoneUtil = null;
        super.tearDown();
    }

    public void testHandyNumberNational() throws NumberParseException {
        String mkHandy = "01742521286";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        String expected = "0174 2521286";
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);

        mkHandy = "0174-2521286";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);

        mkHandy = " 0174 25 21 28 6";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);
    }

    public void testHandyNumberInternational() throws NumberParseException {
        String expected = "+49 174 2521286";

        String mkHandy = "01742521286";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);

        mkHandy = "0049174-2521286";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);

        mkHandy = " +49 174 25 21 28 6";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);
    }

    public void testHandyNumberE164() throws NumberParseException {
        String expected = "+491742521286";

        String mkHandy = "01742521286";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);

        mkHandy = "0049174-2521286";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);

        mkHandy = " +49 174 25 21 28 6";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);
    }

    public void testHandyNumberRFC3966() throws NumberParseException {
        String expected = "tel:+49-174-2521286";

        String mkHandy = "01742521286";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");         
        assertTrue(phoneUtil.isValidNumber(phoneNumber));
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);

        mkHandy = "0049174-2521286";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);

        mkHandy = " +49 174 25 21 28 6";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);
    }
    public void testNumberNational() throws NumberParseException {
        String mkHandy = "05112618291";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));

        String expected = "0511 2618291";
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);

        mkHandy = "0511-2618291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);

        mkHandy = " 0511 26 18 291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        assertEquals(expected, actual);
    }

    public void testNumberInternational() throws NumberParseException {
        String expected = "+49 511 2618291";

        String mkHandy = "05112618291";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);

        mkHandy = "0049511-2618291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);

        mkHandy = " +49 511 26 18 291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertEquals(expected, actual);
    }

    public void testNumberE164() throws NumberParseException {
        String expected = "+495112618291";

        String mkHandy = "05112618291";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mkHandy, "DE");
         assertTrue(phoneUtil.isValidNumber(phoneNumber));
        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);

        mkHandy = "0049511-2618291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);

        mkHandy = " +49 511 26 18 291";
        phoneNumber = phoneUtil.parse(mkHandy, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        assertEquals(expected, actual);
    }

    public void testNumberRFC3966() throws NumberParseException {
        String expected = "tel:+49-511-2618291";

        String phone = "05112618291";
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        String actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);

        phone = "0049511-2618291";
        phoneNumber = phoneUtil.parse(phone, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);

        phone = " +49 511 26 18 291";
        phoneNumber = phoneUtil.parse(phone, "DE");
        assertTrue(phoneUtil.isValidNumber(phoneNumber));

        actual = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.RFC3966);
        assertEquals(expected, actual);
    }
}
