package ps.gov.epsilon.hr.enums.employmentService.v1

/**
 * Created by IntelliJ IDEA.
 * User: MohammadKH
 * Date: 11/20/12
 * Time: 9:41 AM
 */
public enum EnumServiceListType {

    //USED FOR THE MANUAL ADDITION
    END_OF_SERVICE("END_OF_SERVICE"),
    RETURN_TO_SERVICE("RETURN_TO_SERVICE")

    final String value;

    EnumServiceListType(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}