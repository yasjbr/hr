package ps.gov.epsilon.hr.enums.dispatch.v1

/**
 * Created by mkharma on 29/03/17.
 */
enum EnumDispatchListType {

    DISPATCH("DISPATCH"),
    DISPATCH_EXTENSION("DISPATCH_EXTENSION"),
    DISPATCH_STOP("DISPATCH_STOP")



    final String value;

    EnumDispatchListType(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }
}