/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.si.data;

import java.util.Locale;
import javafx.scene.text.Font;

/**
 *
 * @author Mingxing Chen
 */
public class Language {

    // the name used in json data
    private String name;
    // the actual Locale -- standard
    private Locale locale;
    // the text to display to UI
    private String displayLanguage;

    // default font for the language
    private Font font;

    public static final Language EN = new Language("en", Locale.ENGLISH, "ENGLISH", Font.font("Arial", 14));
    public static final Language ZH = new Language("zh", Locale.CHINESE, "中 文", Font.font("KaiTi", 16));

    /**
     * Construct Language default language locale code to "en", with font
     * "Arial" 14
     */
    public Language() {
        this("en");
        font = Font.font("Arial", 14);
    }

    /**
     * Create Language with Locale
     */
    public Language(Locale locale){
        this.locale = locale;
        name = locale.getLanguage();
        displayLanguage = locale.getDisplayLanguage();
    }
    
    /**
     * Construct Language use ISO 639 alpha-2 or alpha-3 language code, the code
     * is will be formatted to lowercase in consistant wiwh java.util.Locale
     * class. The code must be valid for Locale.
     *
     * @param code
     */
    public Language(String code) {
        locale = new Locale(code);
        name = locale.getLanguage(); // language code of this language
        displayLanguage = locale.getDisplayLanguage();
    }

    /**
     * Construct Language with customized name(don't have to be standard),
     * Locale and display language string
     *
     * @param name
     * @param locale
     * @param langString
     */
    public Language(String name, Locale locale, String langString) {
        this.name = name;
        this.locale = locale;
        this.displayLanguage = langString;
       // this.font = Font.font("Arial", 12);
    }

    /**
     * Construct Language with customized name(don't have to be standard),
     * Locale, display string and Font
     *
     * @param name
     * @param locale
     * @param displayLanguage
     * @param font
     */
    public Language(String name, Locale locale, String displayLanguage, Font font) {
        this.name = name;
        this.locale = locale;
        this.displayLanguage = displayLanguage;
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setFontSize(float size) {
        this.font = Font.font(this.font.getFamily(), size);
    }

    /**
     * The same as getName() method, return ISO 639 alpha-2 or alpha-3 language
     * code. alpha-2 is used for most language
     *
     * @return Language name of Locale, eg Locale.ENGLISH return "en", Locale.CHINESE returns "zh"
     */
    public String getDisplayName() {
        return this.name;
    }

    /**
     * Returns a name for the locale's language that is appropriate for display to the user. 
     * If the display language is not set, it will return the Locale's display language.
     * For example, if the locale is en_US, getDisplayLanguage() will return "English"; 
     */
    public String getDisplayLanguage() {
        if (this.displayLanguage == null) {
            return this.locale.getDisplayLanguage();
        }
        return this.displayLanguage;
    }

    /* 
     * Return ISO 639 alpha-2 or alpha-3 language
     * code. alpha-2 is used for most language
     *
     * @return Language name of Locale, eg Locale.ENGLISH return "en", Locale.CHINESE returns "zh"
     */
    public String getName() {
        return name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setDisplayLanguage(String displayLanguage) {
        this.displayLanguage = displayLanguage;
    }
}
