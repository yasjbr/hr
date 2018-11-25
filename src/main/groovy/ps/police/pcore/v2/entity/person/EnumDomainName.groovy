package ps.police.pcore.v2.entity.person

/**
 * Created by wassi on 25/04/18.
 */
enum EnumDomainName {

    LEGAL_IDENTIFIER("ps.police.pcore.v2.entity.legalIdentifier.LegalIdentifier"),
    PERSON_ARREST_HISTORY("ps.police.pcore.v2.entity.person.PersonArrestHistory"),
    PERSON_EDUCATION("ps.police.pcore.v2.entity.person.PersonEducation"),
    PERSON_TRAINING_HISTORY("ps.police.pcore.v2.entity.person.PersonTrainingHistory"),
    PERSON_EMPLOYMENT_HISTORY("ps.police.pcore.v2.entity.person.PersonEmploymentHistory"),
    PERSON_HEALTH_HISTORY("ps.police.pcore.v2.entity.person.PersonHealthHistory"),


    final String domainPath;

    EnumDomainName(String domainPath) {
        this.domainPath = domainPath;
    }

    String toString() {
        domainPath;
    }
}