package ps.gov.epsilon.hr.enums.training.v1

/**
 * Created by mhamaydeh on 4/21/15.
 */
enum EnumCourseNeedPriority {
    VERY_HIGH("VERY_HIGH"),
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW"),
    VERY_LOW("VERY_LOW")

    final String value;

    EnumCourseNeedPriority(String value) {
        this.value = value;
    }

    String toString() {
        value;
    }

    String getKey() {
        name()
    }

}