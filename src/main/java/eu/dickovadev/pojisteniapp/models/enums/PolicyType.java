package eu.dickovadev.pojisteniapp.models.enums;

public enum PolicyType {
    ZIVOTNI_POJISTENI,
    URAZOVE_POJISTENI,
    CESTOVNI_POJISTENI,
    POVINNE_RUCENI,
    HAVARIJNI_POJISTENI,
    POJISTENI_MAJETKU,
    POJISTENI_ODPOVEDNOSTI,
    POJISTENI_MAZLICKA;

    public String getFormattedPolicyType(){
        switch (this){
            case ZIVOTNI_POJISTENI:
                return "Životní pojištění";
            case URAZOVE_POJISTENI:
                return "Úrazové pojištění";
            case CESTOVNI_POJISTENI:
                return "Cestovní pojištění";
            case POVINNE_RUCENI:
                return "Povinné ručení";
            case HAVARIJNI_POJISTENI:
                return "Havarijní pojištění";
            case POJISTENI_MAJETKU:
                return "Pojištění majetku";
            case POJISTENI_ODPOVEDNOSTI:
                return "Pojištění odpovědnosti";
            case POJISTENI_MAZLICKA:
                return "Pojištění mazlíčka";
            default:
                return "Neznámý typ pojištění";
        }
    }

}
