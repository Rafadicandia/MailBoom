package com.mailboom.api.domain.model.whatsapp.valueobjects;

public enum Languajes {
    DEFAULT("default"),
    AFRIKAANS("af"),
    ALBANIAN("sq"),
    ARABIC("ar"),
    AZERBAIJANI("az"),
    BENGALI("bn"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE_CHN("zh_CN"),
    CHINESE_HKG("zh_HK"),
    CHINESE_TAI("zh_TW"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ENGLISH_UK("en_GB"),
    ENGLISH_US("en_US"),
    ESTONIAN("et"),
    FILIPINO("fil"),
    FINNISH("fi"),
    FRENCH("fr"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    GUJARATI("gu"),
    HAUSA("ha"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KANNADA("kn"),
    KAZAKH("kk"),
    KINYARWANDA("rw_RW"),
    KOREAN("ko"),
    KYRGYZ("ky_KG"),
    LAO("lo"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    MALAY("ms"),
    MALAYALAM("ml"),
    MARATHI("mr"),
    NORWEGIAN("nb"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE_BR("pt_BR"),
    PORTUGUESE_PT("pt_PT"),
    PUNJABI("pa"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN("sr"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SPANISH_ARG("es_AR"),
    SPANISH_ESP("es_ES"),
    SPANISH_MEX("es_MX"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAMIL("ta"),
    TELUGU("te"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    UZBEK("uz"),
    VIETNAMESE("vi"),
    ZULU("zu")
    ;

    private final String code;

    Languajes(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }
    public static Languajes fromCode(String code) {
        for (Languajes language : values()) {
            if (language.getCode().equals(code)) {
                return language;
            }

            }
        return null;
    }



}
