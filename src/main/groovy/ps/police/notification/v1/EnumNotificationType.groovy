package ps.police.notification.v1

/**
 * Created by wassi on 11/12/17.
 */
enum EnumNotificationType {

    MY_NOTIFICATION(1L),
    WORKFLOW_MESSAGES(2L),
    LIST_MESSAGES(3L),


    final Long value;


    EnumNotificationType(Long value) {
        this.value = value;
    }

    String toString() {
        value.toString();
    }

    String getKey() {
        name()
    }

}